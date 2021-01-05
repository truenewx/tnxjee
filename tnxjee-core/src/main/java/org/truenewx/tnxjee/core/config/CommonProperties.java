package org.truenewx.tnxjee.core.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.core.Strings;

/**
 * Web通用配置属性
 *
 * @author jianglei
 */
@Configuration
@ConfigurationProperties("tnxjee.common")
public class CommonProperties implements InitializingBean {

    private Map<String, AppConfiguration> apps = new HashMap<>();
    private boolean gatewayEnabled;
    private String gatewayUri;

    public Map<String, AppConfiguration> getApps() {
        return this.apps;
    }

    public void setApps(Map<String, AppConfiguration> apps) {
        this.apps = apps;
    }

    public boolean isGatewayEnabled() {
        return this.gatewayEnabled;
    }

    public void setGatewayEnabled(boolean gatewayEnabled) {
        this.gatewayEnabled = gatewayEnabled;
    }

    public String getGatewayUri() {
        return this.gatewayUri;
    }

    public void setGatewayUri(String gatewayUri) {
        this.gatewayUri = gatewayUri;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.gatewayEnabled) {
            if (StringUtils.isNotBlank(this.gatewayUri)) {
                this.apps.values().forEach(app -> {
                    // 应用未配置特殊的网关地址，则用通用网关地址作为应用网关地址
                    if (StringUtils.isBlank(app.getGatewayUri())) {
                        app.setGatewayUri(this.gatewayUri);
                    }
                });
            }
        } else { // 不开启网关访问，则将所有应用的网关地址置为空
            this.apps.values().forEach(app -> {
                app.setGatewayUri(null);
            });
        }
    }

    public AppConfiguration getApp(String name) {
        return name == null ? null : this.apps.get(name);
    }

    public String findAppName(String url, boolean direct) {
        if (url != null) {
            for (Map.Entry<String, AppConfiguration> entry : this.apps.entrySet()) {
                AppConfiguration configuration = entry.getValue();
                String contextUri = configuration.getContextUri(direct);
                if (url.equals(contextUri) || url.startsWith(contextUri + Strings.SLASH) || url
                        .startsWith(contextUri + Strings.WELL) || url.startsWith(contextUri + Strings.QUESTION)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * 获取所有应用URI，提供给cors配置，作为允许跨域访问的地址清单
     *
     * @return 所有应用URI
     */
    public Set<String> getAllAppUris() {
        Set<String> uris = new HashSet<>();
        this.apps.forEach((name, app) -> {
            if (StringUtils.isNotBlank(app.getGatewayUri())) {
                uris.add(app.getGatewayUri());
            }
            uris.add(app.getDirectUri());
        });
        return uris;
    }

    public Map<String, String> getAppContextUriMapping() {
        Map<String, String> urls = new HashMap<>();
        this.apps.forEach((name, app) -> {
            urls.put(name, app.getContextUri(false));
        });
        return urls;
    }


}
