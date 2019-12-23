package org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration;

import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.truenewx.tnxjee.web.controller.spring.security.access.WebAccessDecisionManager;
import org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;

/**
 * WEB安全配置器支持
 */
public abstract class WebSecurityConfigurerSupport extends WebSecurityConfigurerAdapter {

    @Bean
    public WebFilterInvocationSecurityMetadataSource securityMetadataSource() {
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
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected final void applyConfigurers(HttpSecurity http) throws Exception {
        Collection<SecurityConfigurerAdapter> configurers = getApplicationContext()
                .getBeansOfType(SecurityConfigurerAdapter.class).values();
        for (SecurityConfigurerAdapter configurer : configurers) {
            http.apply(configurer);
        }
    }
}
