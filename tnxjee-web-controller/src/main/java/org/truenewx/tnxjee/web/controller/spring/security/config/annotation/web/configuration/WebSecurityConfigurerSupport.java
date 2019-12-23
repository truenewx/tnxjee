package org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration;

import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.truenewx.tnxjee.web.controller.spring.security.access.WebAccessDecisionManager;
import org.truenewx.tnxjee.web.controller.spring.security.config.annotation.HttpSecurityConfigurer;
import org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;

/**
 * WEB安全配置器支持
 */
public abstract class WebSecurityConfigurerSupport extends WebSecurityConfigurerAdapter {

    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new WebFilterInvocationSecurityMetadataSource();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        return new WebAccessDecisionManager();
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity http = getHttp();
        web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
            public void run() {
                FilterSecurityInterceptor securityInterceptor = http
                        .getSharedObject(FilterSecurityInterceptor.class);
                securityInterceptor.setSecurityMetadataSource(securityMetadataSource());
                securityInterceptor.setAccessDecisionManager(accessDecisionManager());
                web.securityInterceptor(securityInterceptor);
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
    }

    protected final void applyConfigurers(HttpSecurity http) throws Exception {
        Collection<HttpSecurityConfigurer> configurers = getApplicationContext()
                .getBeansOfType(HttpSecurityConfigurer.class).values();
        for (HttpSecurityConfigurer configurer : configurers) {
            http.apply(configurer);
        }
    }
}
