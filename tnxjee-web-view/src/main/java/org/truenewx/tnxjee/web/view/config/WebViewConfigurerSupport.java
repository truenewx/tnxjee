package org.truenewx.tnxjee.web.view.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration.WebSecurityConfigurerSupport;
import org.truenewx.tnxjee.web.view.resource.ResourceUrlConfiguration;
import org.truenewx.tnxjee.web.view.servlet.filter.ForbidAccessFilter;
import org.truenewx.tnxjee.web.view.sitemesh.config.BuildableSiteMeshFilter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * WEB视图层配置支持
 */
public abstract class WebViewConfigurerSupport extends WebSecurityConfigurerSupport {

    /**
     * 子类覆写，声明为Bean，以禁止直接访问jsp文件
     */
    public FilterRegistrationBean<ForbidAccessFilter> forbidAccessFilter() {
        FilterRegistrationBean<ForbidAccessFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new ForbidAccessFilter());
        frb.addUrlPatterns("*.jsp");
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return frb;
    }

    /**
     * 子类覆写，声明为Bean，并提供SiteMesh装饰配置
     */
    public FilterRegistrationBean<BuildableSiteMeshFilter> siteMeshFilter(
            Consumer<SiteMeshFilterBuilder> buildConsumer) {
        FilterRegistrationBean<BuildableSiteMeshFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new BuildableSiteMeshFilter(buildConsumer));
        frb.addUrlPatterns("/*");
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 优先级尽量靠前，仅次于禁止访问过滤器，以确保页面被装饰
        return frb;
    }

    /**
     * 读取静态资源URL样式集合，以获取可匿名访问的URL样式集合
     *
     * @param appendedUrlPatterns 额外附加的URL样式集合
     * @return 可匿名访问的URL样式集合
     */
    protected final String[] getAnonymousUrlPatterns(String... appendedUrlPatterns) {
        Set<String> set = new HashSet<>();
        for (String pattern : appendedUrlPatterns) {
            set.add(pattern.trim());
        }
        String[] staticResourcePatterns = ResourceUrlConfiguration
                .getStaticPatterns(getApplicationContext().getEnvironment());
        for (String pattern : staticResourcePatterns) {
            set.add(pattern.trim());
        }
        return set.toArray(new String[set.size()]);
    }

}
