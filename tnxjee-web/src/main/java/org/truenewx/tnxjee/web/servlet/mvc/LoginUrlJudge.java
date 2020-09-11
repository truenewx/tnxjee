package org.truenewx.tnxjee.web.servlet.mvc;

/**
 * 登录地址判断者
 *
 * @author jianglei
 */
public interface LoginUrlJudge {

    /**
     * 判断指定URL是否登录链接
     *
     * @param url URL
     * @return 指定URL是否登录链接
     */
    boolean isLoginUrl(String url);

}
