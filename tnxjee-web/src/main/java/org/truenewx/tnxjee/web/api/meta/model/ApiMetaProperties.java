package org.truenewx.tnxjee.web.api.meta.model;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * API元数据配置属性
 */
@ConfigurationProperties("tnxjee.web.api")
public class ApiMetaProperties {

    private String baseUrl;
    private String loginSuccessRedirectParameter = "_next";
    private Map<String, String> context;

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLoginSuccessRedirectParameter() {
        return this.loginSuccessRedirectParameter;
    }

    public void setLoginSuccessRedirectParameter(String loginSuccessRedirectParameter) {
        this.loginSuccessRedirectParameter = loginSuccessRedirectParameter;
    }

    public Map<String, String> getContext() {
        return this.context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

}
