package org.truenewx.tnxjee.core.config;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;

/**
 * 占位符配置支持。由于占位符配置器在配置属性生效前创建，所以本类下不能使用配置属性
 */
public abstract class PlaceholderConfigurerSupport {

    @Autowired
    private ProfileSupplier profileSupplier;
    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Resource[] locations = getLocations(this.profileSupplier.get());
        if (ArrayUtils.isNotEmpty(locations)) {
            configurer.setLocations(locations);
            configurer.setIgnoreResourceNotFound(true);
            configurer.setLocalOverride(true);
        }
        return configurer;
    }

    protected Resource[] getLocations(String profile) {
        String root = getLocationRoot(profile);
        if (root != null) {
            String path = root + Strings.SLASH + this.appName + ".properties";
            return new Resource[]{ new FileSystemResource(path) };
        }
        return null;
    }

    protected String getLocationRoot(String profile) {
        return null;
    }

}
