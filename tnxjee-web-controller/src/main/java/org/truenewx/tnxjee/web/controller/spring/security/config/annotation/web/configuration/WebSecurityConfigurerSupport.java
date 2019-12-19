package org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.truenewx.tnxjee.web.controller.spring.security.access.WebAccessDecisionManager;
import org.truenewx.tnxjee.web.controller.spring.security.config.annotation.HttpSecurityConfigurer;
import org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * WEB安全配置器支持
 */
public abstract class WebSecurityConfigurerSupport extends WebSecurityConfigurerAdapter {

    @Bean
    public FilterInvocationSecurityMetadataSource metadataSource() {
        return new WebFilterInvocationSecurityMetadataSource();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        return new WebAccessDecisionManager();
    }

    @Bean
    public FilterSecurityInterceptor interceptor(FilterInvocationSecurityMetadataSource metadataSource,
            AccessDecisionManager accessDecisionManager) {
        FilterSecurityInterceptor interceptor = new FilterSecurityInterceptor();
        interceptor.setSecurityMetadataSource(metadataSource);
        interceptor.setAccessDecisionManager(accessDecisionManager);
        return interceptor;
    }

    protected final HttpSecurity applyConfigurer(HttpSecurity http) throws Exception {
        Collection<HttpSecurityConfigurer> configurers = getApplicationContext().getBeansOfType(HttpSecurityConfigurer.class).values();
        for (HttpSecurityConfigurer configurer : configurers) {
            http = http.apply(configurer).and();
        }
        return http;
    }

}
