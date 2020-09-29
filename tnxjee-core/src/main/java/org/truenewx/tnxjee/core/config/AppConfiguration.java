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
    private String uri;
    private String contextPath = Strings.EMPTY;
    private String loginPath = "/login/cas";
    private String logoutPath = "/logout";

    public String getUserType() {
        return this.userType;
    }

    /**
     * @param userType 对应的用户类型，默认为空，表示无用户类型区别
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUri() {
        return this.uri;
    }

    /**
     * @param uri 包含协议、主机地址和可能的端口号的地址，必须指定
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    /**
     * @param contextPath 上下文根路径，默认为空字符串
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


    public String getContextUri() {
        String uri = getUri();
        // 添加上下文根
        if (StringUtils.isNotBlank(this.contextPath) && !Strings.SLASH.equals(this.contextPath)) {
            // 确保上下文根以/开头
            if (!this.contextPath.startsWith(Strings.SLASH)) {
                uri += Strings.SLASH;
            }
            uri += this.contextPath;
        }
        return uri;
    }

    public String getLoginProcessUrl() {
        return getContextUri() + getLoginPath();
    }

    public String getLogoutProcessUrl() {
        return getContextUri() + getLogoutPath();
    }

}
