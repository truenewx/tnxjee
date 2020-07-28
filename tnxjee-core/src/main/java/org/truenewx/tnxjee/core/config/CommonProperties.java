package org.truenewx.tnxjee.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Web通用配置属性
 *
 * @author jianglei
 */
@Configuration
@ConfigurationProperties("tnxjee.common")
public class CommonProperties {

    private Map<String, AppConfiguration> apps = new HashMap<>();
    @Autowired
    private Environment environment;

    public Map<String, AppConfiguration> getApps() {
        return this.apps;
    }

    public void setApps(Map<String, AppConfiguration> apps) {
        this.apps = apps;
    }

    public AppConfiguration getApp(String name) {
        return this.apps.get(name);
    }

    public Map<String, String> getHostUrls() {
        Map<String, String> hosts = new HashMap<>();
        this.apps.forEach((name, app) -> {
            hosts.put(name, app.getHostUrl());
        });
        return hosts;
    }

    public Map<String, String> getRootUrls() {
        Map<String, String> urls = new HashMap<>();
        this.apps.forEach((name, app) -> {
            urls.put(name, app.getContextUrl());
        });
        return urls;
    }

    public String getSelfAppName() {
        return this.environment.getRequiredProperty("spring.application.name");
    }

    public AppConfiguration getSelfApp() {
        return this.apps.get(getSelfAppName());
    }

}
