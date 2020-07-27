package org.truenewx.tnxjee.core.config;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * Web应用配置
 *
 * @author jianglei
 */
public class WebAppConfiguration {

    private String userType;
    private boolean https;
    private String host;
    private String contextPath = Strings.SLASH;
    private String loginAction = "/login/cas";
    private String logoutAction = "/logout";

    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isHttps() {
        return this.https;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getLoginAction() {
        return this.loginAction;
    }

    public void setLoginAction(String loginAction) {
        this.loginAction = loginAction;
    }

    public String getLogoutAction() {
        return this.logoutAction;
    }

    public void setLogoutAction(String logoutAction) {
        this.logoutAction = logoutAction;
    }

    public String getProtocolHost() {
        String url = "http";
        if (this.https) {
            url += "s";
        }
        url += "://" + this.host;
        return url;
    }

    public String getRootUrl() {
        String url = getProtocolHost();
        // 添加上下文根
        if (StringUtils.isNotBlank(this.contextPath) && !Strings.SLASH.equals(this.contextPath)) {
            // 确保上下文根以/开头
            if (!this.contextPath.startsWith(Strings.SLASH)) {
                url += Strings.SLASH;
            }
            url += this.contextPath;
        }
        return url;
    }

    public String getLoginUrl() {
        return getRootUrl() + this.loginAction;
    }

    public String getLogoutUrl() {
        return getRootUrl() + this.logoutAction;
    }

}
