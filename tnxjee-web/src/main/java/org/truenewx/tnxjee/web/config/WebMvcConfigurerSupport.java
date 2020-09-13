package org.truenewx.tnxjee.web.config;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.web.cors.CorsRegistryProperties;
import org.truenewx.tnxjee.web.util.SwaggerUtil;
import org.truenewx.tnxjee.web.util.WebConstants;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WEB MVC配置器支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
public abstract class WebMvcConfigurerSupport implements WebMvcConfigurer {
    @Autowired
    private CommonProperties commonProperties;
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
            String[] commonHosts = this.commonProperties.getHostUrls().values().toArray(new String[0]);
            String[] allowedOriginals = ArrayUtils.addAll(commonHosts, this.corsRegistryProperties.getAllowedOrigins());
            CorsRegistration registration = registry
                    .addMapping(this.corsRegistryProperties.getPathPattern())
                    .allowedOrigins(allowedOriginals)
                    .allowedMethods(this.corsRegistryProperties.getAllowedMethods())
                    .allowedHeaders(this.corsRegistryProperties.getAllowedHeaders())
                    .allowCredentials(this.corsRegistryProperties.getAllowCredentials());
            String[] exposedHeaders = this.corsRegistryProperties.getExposedHeaders();
            Set<String> exposedHeaderSet = new HashSet<>();
            addExposedHeaders(exposedHeaderSet);
            exposedHeaders = ArrayUtils.addAll(exposedHeaders, exposedHeaderSet.toArray(new String[0]));
            registration.exposedHeaders(exposedHeaders);
            if (this.corsRegistryProperties.getMaxAge() != null) {
                registration.maxAge(this.corsRegistryProperties.getMaxAge());
            }
        }
    }

    protected void addExposedHeaders(Collection<String> exposedHeaders) {
        exposedHeaders.add(WebConstants.HEADER_REDIRECT_TO);
        exposedHeaders.add(WebConstants.HEADER_LOGIN_URL);
        exposedHeaders.add(WebConstants.HEADER_ORIGINAL_REQUEST);
    }

}
