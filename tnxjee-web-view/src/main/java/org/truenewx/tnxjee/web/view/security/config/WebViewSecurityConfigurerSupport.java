package org.truenewx.tnxjee.web.view.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.truenewx.tnxjee.web.security.config.WebSecurityConfigurerSupport;
import org.truenewx.tnxjee.web.security.web.access.BusinessExceptionAccessDeniedHandler;
import org.truenewx.tnxjee.web.view.exception.resolver.ViewBusinessExceptionResolver;
import org.truenewx.tnxjee.web.view.resource.ResourceProperties;

import java.util.Collection;

/**
 * WEB视图层安全配置支持
 */
@EnableConfigurationProperties(ResourceProperties.class)
public abstract class WebViewSecurityConfigurerSupport extends WebSecurityConfigurerSupport {

    @Autowired
    private ViewBusinessExceptionResolver viewBusinessExceptionResolver;
    @Autowired
    private ResourceProperties resourceProperties;

    @Bean
    @Override
    public AccessDeniedHandler accessDeniedHandler() {
        BusinessExceptionAccessDeniedHandler accessDeniedHandler = (BusinessExceptionAccessDeniedHandler) super.accessDeniedHandler();
        accessDeniedHandler.setErrorPage(this.viewBusinessExceptionResolver.getErrorPath());
        return accessDeniedHandler;
    }

    @Override
    protected Collection<RequestMatcher> getAnonymousRequestMatchers() {
        Collection<RequestMatcher> matchers = super.getAnonymousRequestMatchers();
        // 静态资源全部可匿名访问
        String[] staticResourcePatterns = this.resourceProperties.getStaticPatterns();
        for (String pattern : staticResourcePatterns) {
            matchers.add(new AntPathRequestMatcher(pattern));
        }
        return matchers;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.logout().logoutSuccessUrl(getLoginUrl());
    }
}
