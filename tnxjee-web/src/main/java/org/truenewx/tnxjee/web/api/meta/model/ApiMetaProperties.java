package org.truenewx.tnxjee.web.api.meta.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * API元数据配置属性
 */
@Configuration
@ConfigurationProperties("tnxjee.web.api")
public class ApiMetaProperties {

    private String baseUrl;
    private String loginSuccessRedirectParameter = "_next";
    private String[] appNames;

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

    public String[] getAppNames() {
        return this.appNames;
    }

    public void setAppNames(String[] appNames) {
        this.appNames = appNames;
    }

}
