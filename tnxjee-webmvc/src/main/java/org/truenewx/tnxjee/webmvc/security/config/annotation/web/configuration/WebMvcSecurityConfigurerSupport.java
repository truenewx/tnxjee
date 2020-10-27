package org.truenewx.tnxjee.webmvc.security.config.annotation.web.configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.cors.CorsRegistryProperties;
import org.truenewx.tnxjee.web.security.WebSecurityProperties;
import org.truenewx.tnxjee.web.util.SwaggerUtil;
import org.truenewx.tnxjee.webmvc.api.meta.ApiMetaController;
import org.truenewx.tnxjee.webmvc.security.access.UserAuthorityAccessDecisionManager;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.security.web.access.AccessDeniedBusinessExceptionHandler;
import org.truenewx.tnxjee.webmvc.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;
import org.truenewx.tnxjee.webmvc.security.web.authentication.InternalJwtAuthenticationFilter;
import org.truenewx.tnxjee.webmvc.security.web.authentication.WebAuthenticationEntryPoint;
import org.truenewx.tnxjee.webmvc.servlet.mvc.LoginUrlResolver;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;

/**
 * WebMvc安全配置器支持
 */
// 安全配置器与MVC配置器如果合并在同一个类中，webmvc-view工程启动时无法即时注入配置属性实例，导致启动失败
@EnableWebSecurity
public abstract class WebMvcSecurityConfigurerSupport extends WebSecurityConfigurerAdapter implements LoginUrlResolver {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private RedirectStrategy redirectStrategy;
    @Autowired
    private WebSecurityProperties securityProperties;
    @Autowired
    private CorsRegistryProperties corsRegistryProperties;

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
    public WebAuthenticationEntryPoint authenticationEntryPoint() {
        return new WebAuthenticationEntryPoint(getLoginFormUrl());
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

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        SimpleUrlLogoutSuccessHandler successHandler = new SimpleUrlLogoutSuccessHandler();
        successHandler.setRedirectStrategy(this.redirectStrategy);
        String logoutSuccessUrl = getLogoutSuccessUrl();
        if (logoutSuccessUrl != null) {
            successHandler.setDefaultTargetUrl(logoutSuccessUrl);
        }
        return successHandler;
    }

    protected String getLogoutSuccessUrl() {
        return null;
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity http = getHttp();

        web.addSecurityFilterChainBuilder(http).postBuildAction(() -> {
            FilterSecurityInterceptor interceptor = http.getSharedObject(FilterSecurityInterceptor.class);
            WebFilterInvocationSecurityMetadataSource metadataSource = securityMetadataSource();
            FilterInvocationSecurityMetadataSource originalMetadataSource = interceptor.getSecurityMetadataSource();
            if (!(originalMetadataSource instanceof WebFilterInvocationSecurityMetadataSource)) {
                metadataSource.setOrigin(originalMetadataSource);
            }
            interceptor.setSecurityMetadataSource(metadataSource);
            interceptor.setAccessDecisionManager(accessDecisionManager());
            web.securityInterceptor(interceptor);
        });

        Collection<String> ignoringAntPatterns = getIgnoringAntPatterns();
        web.ignoring().antMatchers(ignoringAntPatterns.toArray(new String[ignoringAntPatterns.size()]));
    }

    /**
     * 获取安全框架忽略的URL ANT样式集合
     *
     * @return 安全框架忽略的URL ANT样式集合
     */
    protected Collection<String> getIgnoringAntPatterns() {
        Collection<String> patterns = new HashSet<>();
        RequestMapping mapping = ApiMetaController.class.getAnnotation(RequestMapping.class);
        patterns.add(mapping.value()[0] + "/**");

        if (SwaggerUtil.isEnabled(getApplicationContext())) {
            patterns.add("/swagger-ui.html");
            patterns.add("/webjars/**");
            patterns.add("/v2/api-docs");
            patterns.add("/swagger-resources/**");
        }

        if (this.securityProperties != null) {
            List<String> ignoringPatterns = this.securityProperties.getIgnoringPatterns();
            if (ignoringPatterns != null) {
                patterns.addAll(ignoringPatterns);
            }
        }
        return patterns;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 覆盖父类的方法实现，且不调用父类方法实现，以标记AuthenticationManager由自定义创建，避免创建多个实例
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void configure(HttpSecurity http) throws Exception {
        // 应用登录配置器
        Collection<SecurityConfigurerAdapter> configurers = getSecurityConfigurerAdapters();
        for (SecurityConfigurerAdapter configurer : configurers) {
            http.apply(configurer);
        }
        http.addFilterAfter(new InternalJwtAuthenticationFilter(getApplicationContext()),
                UsernamePasswordAuthenticationFilter.class);

        Collection<RequestMatcher> anonymousMatcherCollection = getAnonymousRequestMatchers();
        RequestMatcher[] anonymousMatchers = anonymousMatcherCollection
                .toArray(new RequestMatcher[anonymousMatcherCollection.size()]);
        // @formatter:off
        http.authorizeRequests().requestMatchers(anonymousMatchers).permitAll().anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler()).and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(getLogoutProcessUrl())) // 不限定POST请求
                .logoutSuccessHandler(logoutSuccessHandler()).deleteCookies(getLogoutClearCookies()).permitAll();
        configure(http.logout());
        // @formatter:on
        if (this.corsRegistryProperties.isAllowCredentials()) {
            http.cors().and().csrf().disable(); // 开启cors则必须关闭csrf，以允许跨站点请求
        } else if (this.securityProperties.isCsrfDisabled()) {
            http.csrf().disable();
        }
    }

    @SuppressWarnings({ "rawtypes" })
    protected Collection<SecurityConfigurerAdapter> getSecurityConfigurerAdapters() {
        return getApplicationContext().getBeansOfType(SecurityConfigurerAdapter.class).values();
    }

    /**
     * 配置登出
     *
     * @param logoutConfigurer 登出配置器
     */
    protected void configure(LogoutConfigurer<HttpSecurity> logoutConfigurer) {
    }

    /**
     * 获取经过安全框架控制，允许匿名访问的请求匹配器集合
     *
     * @return 可匿名访问的请求匹配器集合
     */
    protected Collection<RequestMatcher> getAnonymousRequestMatchers() {
        List<RequestMatcher> matchers = new ArrayList<>();
        matchers.add(new AntPathRequestMatcher("/error/**"));
        // 打开登录表单页面和登出的请求始终可匿名访问
        // 注意：不能将请求URL加入忽略清单中，如果加入，则请求将无法经过安全框架过滤器处理
        matchers.add(new AntPathRequestMatcher(getLoginFormUrl(), HttpMethod.GET.name()));
        matchers.add(new AntPathRequestMatcher(getLogoutProcessUrl()));

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

    @Override
    public String getLoginFormUrl() {
        return "/login";
    }

    @Override
    public boolean isLoginUrl(String url) {
        return url.startsWith(getLoginFormUrl());
    }

    protected String[] getLogoutClearCookies() {
        return new String[]{ "JSESSIONID", "SESSION" };
    }

    /**
     * @return 登出处理地址
     */
    protected String getLogoutProcessUrl() {
        return "/logout";
    }

}
