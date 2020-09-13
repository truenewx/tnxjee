package org.truenewx.tnxjee.web.util;

/**
 * WEB常量类
 */
public class WebConstants {

    private WebConstants() {
    }

    /**
     * 头信息名：AJAX请求
     */
    public static final String HEADER_AJAX_REQUEST = "X-Requested-With";

    /**
     * 头信息名：重定向目标地址
     */
    public static final String HEADER_REDIRECT_TO = "Redirect-To";

    /**
     * 头信息：登录地址
     */
    public static final String HEADER_LOGIN_URL = "Login-Url";
    /**
     * 头信息：原始请求
     */
    public static final String HEADER_ORIGINAL_REQUEST = "Original-Request";

    /**
     * 头信息名：内部RPC
     */
    public static final String HEADER_INTERNAL_RPC = "Internal-Rpc";

}
