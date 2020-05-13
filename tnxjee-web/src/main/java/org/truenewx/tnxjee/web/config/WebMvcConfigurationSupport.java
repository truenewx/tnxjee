package org.truenewx.tnxjee.web.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.web.cors.CorsRegistryProperties;
import org.truenewx.tnxjee.web.http.session.HeaderSessionIdFilter;
import org.truenewx.tnxjee.web.http.session.HeaderSessionIdReader;
import org.truenewx.tnxjee.web.util.SwaggerUtil;

/**
 * WEB MVC配置支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
@EnableConfigurationProperties({ CorsRegistryProperties.class })
public abstract class WebMvcConfigurationSupport implements WebMvcConfigurer {

    @Autowired(required = false)
    private CorsRegistryProperties corsRegistryProperties;
    @Autowired
    private ApplicationContext applicationContext;

    protected final ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        CollectionUtil.remove(converters, converter -> {
            // 移除多余的MappingJackson2HttpMessageConverter，已被JacksonHttpMessageConverter取代
            return converter.getClass() == MappingJackson2HttpMessageConverter.class;
        });
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (SwaggerUtil.isEnabled(getApplicationContext())) {
            registry.addResourceHandler("/swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (this.corsRegistryProperties != null && this.corsRegistryProperties.isEnabled()) {
            CorsRegistration registration = registry.addMapping(this.corsRegistryProperties.getPathPattern())
                    .allowedOrigins(this.corsRegistryProperties.getAllowedOrigins())
                    .allowedMethods(this.corsRegistryProperties.getAllowedMethods())
                    .allowedHeaders(this.corsRegistryProperties.getAllowedHeaders())
                    .allowCredentials(this.corsRegistryProperties.getAllowCredentials());
            String[] exposedHeaders = this.corsRegistryProperties.getExposedHeaders();
            Set<String> set = new HashSet<>();
            Collections.addAll(set, exposedHeaders);
            set.add("redirect");
            exposedHeaders = set.toArray(new String[set.size()]);
            registration.exposedHeaders(exposedHeaders);
            if (this.corsRegistryProperties.getMaxAge() != null) {
                registration.maxAge(this.corsRegistryProperties.getMaxAge());
            }
        }
    }

    @Bean // 在更复杂的会话管理机制引入后可能需要调整生成策略
    @ConditionalOnBean(SessionRepositoryFilter.class)
    public HeaderSessionIdReader headerSessionIdReader() {
        return new HeaderSessionIdReader();
    }

    @Bean
    @ConditionalOnBean(SessionRepositoryFilter.class)
    public FilterRegistrationBean<HeaderSessionIdFilter> headerSessionIdFilter(
            HeaderSessionIdReader headerSessionIdReader,
            SessionRepositoryFilter<?> sessionRepositoryFilter) {
        FilterRegistrationBean<HeaderSessionIdFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new HeaderSessionIdFilter(headerSessionIdReader, sessionRepositoryFilter));
        frb.addUrlPatterns("/*");
        frb.setOrder(HeaderSessionIdFilter.DEFAULT_ORDER);
        return frb;
    }

}
