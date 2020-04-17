package org.truenewx.tnxjee.web.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 资源映射配置属性
 */
@ConfigurationProperties(prefix = "tnxjee.web.resource")
public class ResourceMappingProperties {

    private Map<String, String[]> mapping;

    public Map<String, String[]> getMapping() {
        return this.mapping;
    }

    public void setMapping(Map<String, String[]> mapping) {
        this.mapping = mapping;
    }
}
