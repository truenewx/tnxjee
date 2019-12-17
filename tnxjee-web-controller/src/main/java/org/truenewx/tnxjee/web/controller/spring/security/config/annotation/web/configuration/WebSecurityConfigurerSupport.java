package org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerMapping;
import org.truenewx.tnxjee.core.spring.util.SpringUtil;
import org.truenewx.tnxjee.web.controller.spring.security.access.WebAccessDecisionManager;
import org.truenewx.tnxjee.web.controller.spring.security.user.UserAuthenticationService;
import org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;

/**
 * WEB安全配置
 */
@EnableWebSecurity
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserAuthenticationService userAuthenticationService = SpringUtil.getFirstBeanByClass(getApplicationContext(), UserAuthenticationService.class);
        Assert.notNull(userAuthenticationService, "Can not find bean of type: UserAuthenticationService");
        auth.userDetailsService(userAuthenticationService);
        auth.authenticationProvider(userAuthenticationService);
    }

}
