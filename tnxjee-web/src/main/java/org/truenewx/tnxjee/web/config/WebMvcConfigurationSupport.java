package org.truenewx.tnxjee.web.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.web.cors.CorsRegistryProperties;
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
        if (this.corsRegistryProperties != null) {
            if (this.corsRegistryProperties.isEnabled()) {
                CorsRegistration registration = registry.addMapping(this.corsRegistryProperties.getPathPattern())
                        .allowedOrigins(this.corsRegistryProperties.getAllowedOrigins())
                        .allowedMethods(this.corsRegistryProperties.getAllowedMethods())
                        .allowedHeaders(this.corsRegistryProperties.getAllowedHeaders())
                        .allowCredentials(this.corsRegistryProperties.getAllowCredentials())
                        .exposedHeaders(this.corsRegistryProperties.getExposedHeaders());
                if (this.corsRegistryProperties.getMaxAge() != null) {
                    registration.maxAge(this.corsRegistryProperties.getMaxAge());
                }
            }
        }
    }

}
