package org.truenewx.tnxjee.web.view.spring.security.config.annotation.web.configuration;

import org.truenewx.tnxjee.web.controller.spring.security.config.annotation.web.configuration.WebSecurityConfigurerSupport;
import org.truenewx.tnxjee.web.view.resource.ResourceUrlConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * WEB视图层安全配置器支持
 */
public abstract class WebViewSecurityConfigurerSupport extends WebSecurityConfigurerSupport {

    protected final String[] getAnonymousPatterns(String... appendedPatterns) {
        Set<String> set = new HashSet<>();
        for (String pattern : appendedPatterns) {
            set.add(pattern.trim());
        }
        String[] staticResourcePatterns = ResourceUrlConfiguration
                .getStaticPatterns(getApplicationContext().getEnvironment());
        for (String pattern : staticResourcePatterns) {
            set.add(pattern.trim());
        }
        return set.toArray(new String[set.size()]);
    }

}
