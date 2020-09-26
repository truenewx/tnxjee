package org.truenewx.tnxjee.webmvc.api.meta.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * API元数据配置属性
 */
@Configuration
@ConfigurationProperties("tnxjee.web.api")
public class ApiMetaProperties {

    private String[] appNames;
    private String baseApp;
    private String loginSuccessRedirectParameter = "_next";

    public String[] getAppNames() {
        return this.appNames;
    }

    /**
     * @param appNames 相关应用的名称集，默认为整个系统所有应用
     */
    public void setAppNames(String[] appNames) {
        this.appNames = appNames;
    }

    public String getBaseApp() {
        return this.baseApp;
    }

    /**
     * @param baseApp 基础应用的名称
     */
    public void setBaseApp(String baseApp) {
        this.baseApp = baseApp;
    }

    public String getLoginSuccessRedirectParameter() {
        return this.loginSuccessRedirectParameter;
    }

    public void setLoginSuccessRedirectParameter(String loginSuccessRedirectParameter) {
        this.loginSuccessRedirectParameter = loginSuccessRedirectParameter;
    }

}
