package org.truenewx.tnxjee.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.web.cors.CorsRegistryProperties;
import org.truenewx.tnxjee.web.resource.ResourceMappingProperties;

import java.util.Map;

/**
 * WEB MVC配置支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
@EnableConfigurationProperties({ ResourceMappingProperties.class, CorsRegistryProperties.class })
public abstract class WebMvcConfigurationSupport implements WebMvcConfigurer {

    @Autowired(required = false)
    private ResourceMappingProperties resourceMappingProperties;

    @Autowired(required = false)
    private CorsRegistryProperties corsRegistryProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (this.resourceMappingProperties != null) {
            Map<String, String[]> mapping = this.resourceMappingProperties.getMapping();
            if (mapping != null) {
                mapping.forEach((pattern, locations) -> {
                    registry.addResourceHandler(pattern)
                            .addResourceLocations(locations);
                });
            }
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
