package org.truenewx.tnxjee.web.api.meta.model;

import java.util.HashMap;
import java.util.Map;

/**
 * API上下文环境
 */
public class ApiContext {

    private String baseUrl;
    private String loginSuccessRedirectParameter = "_next";
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> apps = new HashMap<>();

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

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getApps() {
        return this.apps;
    }

}
