package org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
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
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.controller.spring.security.access.UserAuthorityAccessDecisionManager;
import org.truenewx.tnxjee.web.controller.spring.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.web.controller.spring.security.web.access.BusinessExceptionAccessDeniedHandler;
import org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;
import org.truenewx.tnxjee.web.controller.spring.security.web.authentication.WebAuthenticationEntryPoint;
import org.truenewx.tnxjee.web.controller.spring.web.servlet.HandlerMethodMapping;

/**
 * WEB安全配置器支持
 */
@EnableWebSecurity
public abstract class WebSecurityConfigurerSupport extends WebSecurityConfigurerAdapter {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    @Bean
    public WebFilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new WebFilterInvocationSecurityMetadataSource();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new WebAuthenticationEntryPoint(getLoginUrl());
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        return new UserAuthorityAccessDecisionManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new BusinessExceptionAccessDeniedHandler();
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity http = getHttp();
        web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
            @Override
            public void run() {
                FilterSecurityInterceptor interceptor = http.getSharedObject(FilterSecurityInterceptor.class);
                WebFilterInvocationSecurityMetadataSource metadataSource = securityMetadataSource();
                FilterInvocationSecurityMetadataSource originalMetadataSource = interceptor.getSecurityMetadataSource();
                if (!(originalMetadataSource instanceof WebFilterInvocationSecurityMetadataSource)) {
                    metadataSource.setOrigin(originalMetadataSource);
                }
                interceptor.setSecurityMetadataSource(metadataSource);
                interceptor.setAccessDecisionManager(accessDecisionManager());
                web.securityInterceptor(interceptor);
            }
        });
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 覆盖父类的方法实现，且不调用父类方法实现，以标记AuthenticationManager由自定义创建，避免创建多个实例
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        applyConfigurers(http);
        Collection<RequestMatcher> anonymousMatcherCollection = getAnonymousRequestMatchers();
        RequestMatcher[] anonymousMatchers = anonymousMatcherCollection
                .toArray(new RequestMatcher[anonymousMatcherCollection.size()]);
        // @formatter:off
        http.authorizeRequests()
                .requestMatchers(anonymousMatchers).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher(getLogoutUrl())) // 不限定POST请求
                .deleteCookies("JSESSIONID").permitAll();
        // @formatter:on
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected final void applyConfigurers(HttpSecurity http) throws Exception {
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
        matchers.add(new AntPathRequestMatcher(getLoginUrl()));
        matchers.add(new AntPathRequestMatcher("/error/**"));

        this.handlerMethodMapping.getAllHandlerMethods().forEach((action, handlerMethod) -> {
            if (isAnonymous(handlerMethod.getMethod())) {
                String pattern = action.getUri().replaceAll("\\{\\S+\\}", Strings.ASTERISK);
                HttpMethod httpMethod = action.getMethod();
                String method = httpMethod == null ? null : httpMethod.name();
                matchers.add(new AntPathRequestMatcher(pattern, method));
            }
        });

        return matchers;
    }

    private boolean isAnonymous(Method method) {
        if (Modifier.isPublic(method.getModifiers())) {
            ConfigAuthority[] configAuthorities = method.getAnnotationsByType(ConfigAuthority.class);
            for (ConfigAuthority configAuthority : configAuthorities) {
                if (configAuthority.anonymous()) { // 有一个匿名配置，即视为匿名
                    return true;
                }
            }
        }
        return false;
    }

    protected String getLoginUrl() {
        return "/login";
    }

    protected String getLogoutUrl() {
        return "/logout";
    }

}
