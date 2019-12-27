package org.truenewx.tnxjee.web.view.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration.WebSecurityConfigurerSupport;
import org.truenewx.tnxjee.web.controller.spring.security.web.access.BusinessExceptionAccessDeniedHandler;
import org.truenewx.tnxjee.web.view.exception.resolver.ViewBusinessExceptionResolver;
import org.truenewx.tnxjee.web.view.resource.ResourceUrlConfiguration;
import org.truenewx.tnxjee.web.view.servlet.filter.ForbidAccessFilter;
import org.truenewx.tnxjee.web.view.sitemesh.config.BuildableSiteMeshFilter;

import javax.servlet.DispatcherType;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * WEB视图层配置支持
 */
public abstract class WebViewConfigurerSupport extends WebSecurityConfigurerSupport {

    @Autowired
    private ViewBusinessExceptionResolver viewBusinessExceptionResolver;

    @Bean
    public FilterRegistrationBean<ForbidAccessFilter> forbidAccessFilter() {
        FilterRegistrationBean<ForbidAccessFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new ForbidAccessFilter());
        frb.addUrlPatterns("*.jsp");
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return frb;
    }

    protected FilterRegistrationBean<BuildableSiteMeshFilter> siteMeshFilter(
            Consumer<SiteMeshFilterBuilder> buildConsumer) {
        FilterRegistrationBean<BuildableSiteMeshFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new BuildableSiteMeshFilter(buildConsumer));
        frb.addUrlPatterns("/*");
        frb.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.ERROR);
        return frb;
    }

    @Bean
    @Override
    public AccessDeniedHandler accessDeniedHandler() {
        BusinessExceptionAccessDeniedHandler accessDeniedHandler = (BusinessExceptionAccessDeniedHandler) super.accessDeniedHandler();
        accessDeniedHandler.setErrorPage(this.viewBusinessExceptionResolver.getErrorPath());
        return accessDeniedHandler;
    }

    protected String[] getAnonymousUrlPatterns() {
        Set<String> set = new HashSet<>();
        for (String pattern : super.getAnonymousUrlPatterns()) {
            set.add(pattern.trim());
        }
        // 静态资源全部可匿名访问
        String[] staticResourcePatterns = ResourceUrlConfiguration
                .getStaticPatterns(getApplicationContext().getEnvironment());
        for (String pattern : staticResourcePatterns) {
            set.add(pattern.trim());
        }
        return set.toArray(new String[set.size()]);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.logout().logoutSuccessUrl(getLoginUrl());
    }

}
