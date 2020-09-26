package org.truenewx.tnxjee.webmvc.servlet.mvc;

/**
 * 登录地址解决器
 *
 * @author jianglei
 */
public interface LoginUrlResolver {

    /**
     * @return 登录表单地址
     */
    String getLoginFormUrl();

    /**
     * 判断指定URL是否登录链接
     *
     * @param url URL
     * @return 指定URL是否登录链接
     */
    boolean isLoginUrl(String url);

}
