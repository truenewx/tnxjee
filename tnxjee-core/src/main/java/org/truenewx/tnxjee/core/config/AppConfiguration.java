package org.truenewx.tnxjee.core.config;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 应用配置
 *
 * @author jianglei
 */
public class AppConfiguration {

    private String userType;
    private boolean https;
    private String host;
    private String contextPath = Strings.SLASH;
    private String loginPath = "/login/cas";
    private String logoutPath = "/logout";

    public String getUserType() {
        return this.userType;
    }

    /**
     * @param userType 对应的用户类型，默认为空，表示不限定用户类型
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isHttps() {
        return this.https;
    }

    /**
     * @param https 是否通过https协议访问，默认为false
     */
    public void setHttps(boolean https) {
        this.https = https;
    }

    public String getHost() {
        return this.host;
    }

    /**
     * @param host 主机地址，包含可能的端口号，必须指定
     */
    public void setHost(String host) {
        this.host = host;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    /**
     * @param contextPath 上下文根路径，默认为/
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getLoginPath() {
        return this.loginPath;
    }

    /**
     * @param loginPath 登录的相对路径，默认为/login/cas
     */
    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }

    public String getLogoutPath() {
        return this.logoutPath;
    }

    /**
     * @param logoutPath 登出的相对路径，默认为/logout
     */
    public void setLogoutPath(String logoutPath) {
        this.logoutPath = logoutPath;
    }

    public String getHostUrl() {
        String url = "http";
        if (this.https) {
            url += "s";
        }
        url += "://" + this.host;
        return url;
    }

    public String getContextUrl() {
        String url = getHostUrl();
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
        return getContextUrl() + getLoginPath();
    }

    public String getLogoutUrl() {
        return getContextUrl() + getLogoutPath();
    }

}
