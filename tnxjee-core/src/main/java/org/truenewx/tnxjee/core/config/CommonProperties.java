package org.truenewx.tnxjee.core.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
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
public class CommonProperties implements InitializingBean {

    private Map<String, AppConfiguration> apps = new HashMap<>();
    private String gatewayUri;
    @Autowired
    private Environment environment;

    public Map<String, AppConfiguration> getApps() {
        return this.apps;
    }

    public void setApps(Map<String, AppConfiguration> apps) {
        this.apps = apps;
    }

    public String getGatewayUri() {
        return this.gatewayUri;
    }

    public void setGatewayUri(String gatewayUri) {
        this.gatewayUri = gatewayUri;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(this.gatewayUri)) {
            this.apps.values().forEach(app -> {
                app.setUri(this.gatewayUri);
            });
        }
    }

    public AppConfiguration getApp(String name) {
        return this.apps.get(name);
    }

    public Map<String, String> getAppUris() {
        Map<String, String> uris = new HashMap<>();
        this.apps.forEach((name, app) -> {
            uris.put(name, app.getUri());
        });
        return uris;
    }

    public Map<String, String> getAppContextUris() {
        Map<String, String> urls = new HashMap<>();
        this.apps.forEach((name, app) -> {
            urls.put(name, app.getContextUri());
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
