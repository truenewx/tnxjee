package org.truenewx.tnxjee.web.security.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.security.access.UserAuthorityAccessDecisionManager;
import org.truenewx.tnxjee.web.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.web.security.web.access.AccessDeniedBusinessExceptionHandler;
import org.truenewx.tnxjee.web.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;
import org.truenewx.tnxjee.web.security.web.authentication.WebAuthenticationEntryPoint;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * WEB安全配置器支持
 */
@EnableWebSecurity
public abstract class WebSecurityConfigurerSupport extends WebSecurityConfigurerAdapter {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    /**
     * 获取访问资源需要具备的权限
     */
    @Bean
    public WebFilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new WebFilterInvocationSecurityMetadataSource();
    }

    /**
     * 匿名用户试图访问登录用户才能访问的资源后的错误处理
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new WebAuthenticationEntryPoint(getLoginUrl());
    }

    /**
     * 登录用户访问资源的权限判断
     */
    @Bean
    public UserAuthorityAccessDecisionManager accessDecisionManager() {
        return new UserAuthorityAccessDecisionManager();
    }

    /**
     * 登录用户越权访问资源后的错误处理
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedBusinessExceptionHandler();
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity http = getHttp();

        web.addSecurityFilterChainBuilder(http).postBuildAction(() -> {
            FilterSecurityInterceptor interceptor = http
                    .getSharedObject(FilterSecurityInterceptor.class);
            WebFilterInvocationSecurityMetadataSource metadataSource = securityMetadataSource();
            FilterInvocationSecurityMetadataSource originalMetadataSource = interceptor
                    .getSecurityMetadataSource();
            if (!(originalMetadataSource instanceof WebFilterInvocationSecurityMetadataSource)) {
                metadataSource.setOrigin(originalMetadataSource);
            }
            interceptor.setSecurityMetadataSource(metadataSource);
            interceptor.setAccessDecisionManager(accessDecisionManager());
            web.securityInterceptor(interceptor);
        });

        Collection<String> ignoringAntPatterns = getIgnoringAntPatterns();
        web.ignoring()
                .antMatchers(ignoringAntPatterns.toArray(new String[ignoringAntPatterns.size()]));
    }

    /**
     * 获取安全框架忽略的URL ANT样式集合
     *
     * @return 安全框架忽略的URL ANT样式集合
     */
    protected Collection<String> getIgnoringAntPatterns() {
        Collection<String> patterns = new HashSet<>();
        patterns.add("/api/meta");
        return patterns;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 覆盖父类的方法实现，且不调用父类方法实现，以标记AuthenticationManager由自定义创建，避免创建多个实例
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        applyLoginConfigurers(http);
        Collection<RequestMatcher> anonymousMatcherCollection = getAnonymousRequestMatchers();
        RequestMatcher[] anonymousMatchers = anonymousMatcherCollection
                .toArray(new RequestMatcher[anonymousMatcherCollection.size()]);
        // @formatter:off
        http.authorizeRequests().requestMatchers(anonymousMatchers).permitAll()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
            .accessDeniedHandler(accessDeniedHandler())
            .and()
            .logout().logoutRequestMatcher(new AntPathRequestMatcher(getLogoutUrl())) // 不限定POST请求
            .deleteCookies("JSESSIONID").permitAll();
        if (isCsrfDisabled()) {
            http.csrf().disable();
        }
        // @formatter:on
    }

    /**
     * 判断是否关闭csrf限定，csrf：跨站点请求伪造<br>
     * true-允许其它站点向当前站点发送请求，false-禁止其它站点向当前站点发送请求<br>
     * 默认为true
     *
     * @return 是否关闭csrf限定
     */
    protected boolean isCsrfDisabled() {
        return true;
    }

    /**
     * 加载登录配置
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected final void applyLoginConfigurers(HttpSecurity http) throws Exception {
        Collection<SecurityConfigurerAdapter> configurers = getApplicationContext()
                .getBeansOfType(SecurityConfigurerAdapter.class).values();
        for (SecurityConfigurerAdapter configurer : configurers) {
            http.apply(configurer);
        }
    }

    /**
     * 获取可匿名访问的请求匹配器集合
     *
     * @return 可匿名访问的请求匹配器集合
     */
    protected Collection<RequestMatcher> getAnonymousRequestMatchers() {
        List<RequestMatcher> matchers = new ArrayList<>();
        for (String pattern : getAnonymousAntPatterns()) {
            if (StringUtils.isNotBlank(pattern)) {
                matchers.add(new AntPathRequestMatcher(pattern));
            }
        }

        this.handlerMethodMapping.getAllHandlerMethods().forEach((action, handlerMethod) -> {
            Method method = handlerMethod.getMethod();
            if (Modifier.isPublic(method.getModifiers())) {
                ConfigAnonymous configAnonymous = method.getAnnotation(ConfigAnonymous.class);
                if (configAnonymous != null) {
                    HttpMethod httpMethod = action.getMethod();
                    String methodValue = httpMethod == null ? null : httpMethod.name();
                    RequestMatcher matcher;
                    String regex = configAnonymous.regex();
                    if (StringUtils.isNotBlank(regex)) { // 指定了正则表达式，则采用正则匹配器
                        matcher = new RegexRequestMatcher(regex, methodValue, true);
                    } else {
                        String pattern = action.getUri().replaceAll("\\{\\S+\\}", Strings.ASTERISK);
                        matcher = new AntPathRequestMatcher(pattern, methodValue);
                    }
                    matchers.add(matcher);
                }
            }
        });

        return matchers;
    }

    protected Collection<String> getAnonymousAntPatterns() {
        Set<String> patterns = new HashSet<>();
        Collections.addAll(patterns, getLoginUrl(), "/error/**");
        return patterns;
    }

    protected String getLoginUrl() {
        return "/login";
    }

    protected String getLogoutUrl() {
        return "/logout";
    }

}
