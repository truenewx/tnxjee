package org.truenewx.tnxjee.web.view.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 视图层资源配置属性集
 */
@ConfigurationProperties("tnxjee.web.view.resource")
public class ResourceProperties {

    private static final String[] DEFAULT_RESOURCES_STATIC_PATTERN = {"/assets/**", "/vendor/**", "**/*.css", "**/*.js", "/robots.txt"};

    private String[] staticPatterns = DEFAULT_RESOURCES_STATIC_PATTERN;

    public String[] getStaticPatterns() {
        return this.staticPatterns;
    }

    public void setStaticPatterns(String[] staticPatterns) {
        this.staticPatterns = staticPatterns;
    }

}
