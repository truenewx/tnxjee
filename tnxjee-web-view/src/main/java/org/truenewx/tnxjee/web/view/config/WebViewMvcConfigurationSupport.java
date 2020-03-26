package org.truenewx.tnxjee.web.view.config;

import org.apache.commons.lang3.StringUtils;
import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.config.WebMvcConfigurationSupport;
import org.truenewx.tnxjee.web.view.servlet.filter.ForbidAccessFilter;
import org.truenewx.tnxjee.web.view.sitemesh.config.BuildableSiteMeshFilter;

import javax.servlet.DispatcherType;

/**
 * WEB视图层MVC配置支持
 */
@EnableConfigurationProperties(WebMvcProperties.class)
public abstract class WebViewMvcConfigurationSupport extends WebMvcConfigurationSupport {

    @Autowired
    private WebMvcProperties mvcProperties;

    @Bean
    public FilterRegistrationBean<ForbidAccessFilter> forbidAccessFilter() {
        FilterRegistrationBean<ForbidAccessFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new ForbidAccessFilter());
        frb.addUrlPatterns("*.jsp");
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return frb;
    }

    @Bean
    public FilterRegistrationBean<BuildableSiteMeshFilter> siteMeshFilter() {
        FilterRegistrationBean<BuildableSiteMeshFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new BuildableSiteMeshFilter(this::buildSiteMeshFilter));
        frb.addUrlPatterns("/*");
        frb.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.ERROR);
        return frb;
    }

    protected void buildSiteMeshFilter(SiteMeshFilterBuilder builder) {
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (StringUtils.isNotBlank(staticPathPattern)) {
            String[] staticPathPatterns = StringUtil.splitAndTrim(staticPathPattern, Strings.COMMA);
            for (String pattern : staticPathPatterns) {
                String location = getStaticResourceLocation(pattern);
                registry.addResourceHandler(pattern).addResourceLocations(location);
            }
            registry.setOrder(Ordered.HIGHEST_PRECEDENCE + 2000);
        }
    }

    /**
     * 获取指定静态资源路径Ant样式对应的资源文件存放目录
     *
     * @param pattern 资源路径Ant样式
     * @return 资源文件存放目录
     */
    protected String getStaticResourceLocation(String pattern) {
        int index = pattern.indexOf(Strings.ASTERISK);
        if (index >= 0) {
            pattern = pattern.substring(0, index);
        }
        index = pattern.lastIndexOf(Strings.SLASH);
        if (index >= 0) {
            pattern = pattern.substring(0, index + 1);
        }
        return pattern;
    }

}
