package org.truenewx.tnxjee.web.controller.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录器
 *
 * @author jianglei
 */
public interface Loginer {

    /**
     * 执行登录
     *
     * @param loginName 登录名
     * @param password  密码
     * @param request   HTTP请求
     * @param response  HTTP响应
     * @return 登录成功后跳转的视图
     */
    String login(String loginName, String password, HttpServletRequest request,
            HttpServletResponse response);

    /**
     * 判断指定URL是否登录链接
     *
     * @param url URL
     * @return 指定URL是否登录链接
     */
    boolean isLoginUrl(String url);

}
