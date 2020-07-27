package org.truenewx.tnxjee.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Web通用配置属性
 *
 * @author jianglei
 */
@Configuration
@ConfigurationProperties("tnxjee.web.common")
public class WebCommonProperties {

    private Map<String, WebAppConfiguration> apps = new HashMap<>();

    public Map<String, WebAppConfiguration> getApps() {
        return this.apps;
    }

    public void setApps(Map<String, WebAppConfiguration> apps) {
        this.apps = apps;
    }

    public WebAppConfiguration getApp(String name) {
        return this.apps.get(name);
    }

    public Map<String, String> getProtocolHosts() {
        Map<String, String> hosts = new HashMap<>();
        this.apps.forEach((name, app) -> {
            hosts.put(name, app.getProtocolHost());
        });
        return hosts;
    }

    public Map<String, String> getRootUrls() {
        Map<String, String> urls = new HashMap<>();
        this.apps.forEach((name, app) -> {
            urls.put(name, app.getRootUrl());
        });
        return urls;
    }

}
