package org.truenewx.tnxjee.web.servlet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Servlet上下文配置属性集
 */
@ConfigurationProperties("tnxjee.web.servlet.context")
public class ServletContextProperties {

    private Map<String, String> attributes;

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
