package org.truenewx.tnxjee.web.config;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.web.cors.CorsRegistryProperties;
import org.truenewx.tnxjee.web.util.SwaggerUtil;
import org.truenewx.tnxjee.web.util.WebConstants;

/**
 * WEB MVC配置器支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
public abstract class WebMvcConfigurerSupport implements WebMvcConfigurer {

    @Autowired
    private CorsRegistryProperties corsRegistryProperties;
    @Autowired
    private ApplicationContext applicationContext;

    protected final ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter -> {
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
        if (this.corsRegistryProperties.isEnabled()) {
            CorsRegistration registration = registry
                    .addMapping(this.corsRegistryProperties.getPathPattern())
                    .allowedOrigins(this.corsRegistryProperties.getAllowedOrigins())
                    .allowedMethods(this.corsRegistryProperties.getAllowedMethods())
                    .allowedHeaders(this.corsRegistryProperties.getAllowedHeaders())
                    .allowCredentials(this.corsRegistryProperties.getAllowCredentials());
            String[] exposedHeaders = this.corsRegistryProperties.getExposedHeaders();
            exposedHeaders = ArrayUtils.addAll(exposedHeaders, WebConstants.HEADER_REDIRECT_TO);
            registration.exposedHeaders(exposedHeaders);
            if (this.corsRegistryProperties.getMaxAge() != null) {
                registration.maxAge(this.corsRegistryProperties.getMaxAge());
            }
        }
    }

}
