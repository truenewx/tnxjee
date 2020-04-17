package org.truenewx.tnxjee.web.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("tnxjee.web.security")
public class WebSecurityProperties {

    private List<String> ignoringPatterns;

    public List<String> getIgnoringPatterns() {
        return this.ignoringPatterns;
    }

    public void setIgnoringPatterns(List<String> ignoringPatterns) {
        this.ignoringPatterns = ignoringPatterns;
    }

}
