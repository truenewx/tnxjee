package org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.truenewx.tnxjee.web.controller.spring.security.access.WebAccessDecisionManager;
import org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;
import org.truenewx.tnxjee.web.controller.spring.security.web.authentication.WebAuthenticationEntryPoint;

import java.util.Collection;

/**
 * WEB安全配置器支持
 */
@EnableWebSecurity
public abstract class WebSecurityConfigurerSupport extends WebSecurityConfigurerAdapter {

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
        return new WebAccessDecisionManager();
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
//                interceptor.setAccessDecisionManager(accessDecisionManager());
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
        // @formatter:off
        http.authorizeRequests()
                .antMatchers(getAnonymousUrlPatterns()).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedPage("/error/403")
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
     * 获取可匿名访问的URL样式集合
     *
     * @return 可匿名访问的URL样式集合
     */
    protected String[] getAnonymousUrlPatterns() {
        return new String[]{getLoginUrl(), "/error/**"};
    }

    protected String getLoginUrl() {
        return "/login";
    }

    protected String getLogoutUrl() {
        return "/logout";
    }

}
