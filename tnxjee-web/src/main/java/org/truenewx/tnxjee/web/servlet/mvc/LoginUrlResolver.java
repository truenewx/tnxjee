package org.truenewx.tnxjee.web.servlet.mvc;

/**
 * 登录地址解决器
 *
 * @author jianglei
 */
public interface LoginUrlResolver {

    /**
     * @return 表单登录地址
     */
    String getLoginFormUrl();

    /**
     * @return AJAX登录地址
     */
    String getLoginAjaxUrl();

    /**
     * 判断指定URL是否登录链接
     *
     * @param url URL
     * @return 指定URL是否登录链接
     */
    boolean isLoginUrl(String url);

}
