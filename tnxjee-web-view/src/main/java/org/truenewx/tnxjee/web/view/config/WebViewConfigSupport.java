package org.truenewx.tnxjee.web.view.config;

import java.util.function.Consumer;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.truenewx.tnxjee.web.view.servlet.filter.ForbidAccessFilter;
import org.truenewx.tnxjee.web.view.sitemesh.config.BuildableSiteMeshFilter;
import org.truenewx.tnxjee.web.view.spring.security.config.annotation.web.configuration.WebViewSecurityConfigurerSupport;

/**
 * WEB视图层配置支持
 */
public abstract class WebViewConfigSupport extends WebViewSecurityConfigurerSupport {

    public FilterRegistrationBean<ForbidAccessFilter> forbidAccessFilter() {
        FilterRegistrationBean<ForbidAccessFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new ForbidAccessFilter());
        frb.addUrlPatterns("*.jsp");
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return frb;
    }

    public FilterRegistrationBean<BuildableSiteMeshFilter> siteMeshFilter(
            Consumer<SiteMeshFilterBuilder> buildConsumer) {
        FilterRegistrationBean<BuildableSiteMeshFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new BuildableSiteMeshFilter(buildConsumer));
        frb.addUrlPatterns("/*");
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 优先级尽量靠前，仅次于禁止访问过滤器，以确保页面被装饰
        return frb;
    }

}
