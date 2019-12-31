package org.truenewx.tnxjee.web.view.config;

import java.util.function.Consumer;

import javax.servlet.DispatcherType;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.truenewx.tnxjee.web.view.servlet.filter.ForbidAccessFilter;
import org.truenewx.tnxjee.web.view.sitemesh.config.BuildableSiteMeshFilter;

/**
 * WEB视图层MVC配置支持
 */
public abstract class WebViewMvcConfigurationSupport {

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

}
