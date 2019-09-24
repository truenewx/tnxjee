package org.truenewx.tnxjee.core.spring.context.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

/**
 * 环境属性配置提供者
 *
 * @author jianglei
 */
@Component
public class EnvironmentPropertiesProvider {
    @Autowired
    private ConfigurableEnvironment env;
    private Map<String, Properties> propertiesMapping = new HashMap<>();

    public Properties getProperties(String prefix, boolean withPrefix) {
        Properties properties = this.propertiesMapping.get(prefix);
        if (properties == null) {
            properties = new Properties();
            loadProperties(properties, prefix, withPrefix);
            this.propertiesMapping.put(prefix, properties);
        }
        return properties;
    }

    private void loadProperties(Properties properties, String prefix, boolean withPrefix) {
        int index = withPrefix ? 0 : prefix.length() + 1;
        this.env.getPropertySources().forEach(ps -> {
            Object source = ps.getSource();
            if (source instanceof Map) {
                Map<?, ?> mapSource = (Map<?, ?>) source;
                mapSource.forEach((key, value) -> {
                    if (key instanceof String) {
                        String property = (String) key;
                        if (property.startsWith(prefix)) {
                            property = property.substring(index);
                            properties.put(property, value.toString());
                        }
                    }
                });
            }
        });
    }

}
