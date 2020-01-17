package org.truenewx.tnxjee.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.core.context.EnvironmentPropertiesIterator;

/**
 * 系统属性配置
 */
@Configuration
public class SystemPropertiesConfiguration {

    private static final String SYSTEM_PROPERTY_PREFIX = "system.";

    @Bean
    public EnvironmentPropertiesIterator propertiesIterator() {
        return new EnvironmentPropertiesIterator((name, value) -> {
            if (name.startsWith(SYSTEM_PROPERTY_PREFIX) && value != null) {
                String key = name.substring(SYSTEM_PROPERTY_PREFIX.length());
                System.setProperty(key, value.toString());
            }
        });
    }

}
