package org.truenewx.tnxjee.web.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.truenewx.tnxjee.web.security.web.AjaxRedirectStrategy;

/**
 * WEB未登录访问限制的进入点
 */
public class WebAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired
    private AjaxRedirectStrategy redirectStrategy;
    private String loginAjaxUrl;

    public WebAuthenticationEntryPoint(String loginFormUrl, String loginAjaxUrl) {
        super(loginFormUrl);
        this.loginAjaxUrl = loginAjaxUrl;
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception) {
        if (this.loginAjaxUrl != null && this.redirectStrategy.isAjaxRequest(request)) {
            return this.loginAjaxUrl;
        }
        return getLoginFormUrl();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (this.redirectStrategy.isAjaxRequest(request)) {
            if (this.loginAjaxUrl == null) { // AJAX请求没有跳转目标则返回未授权错误
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else { // AJAX请求有跳转目标则执行特殊的AJAX跳转
                String redirectLoginUrl = buildRedirectUrlToLoginPage(request, response, authException);
                this.redirectStrategy.sendRedirect(request, response, redirectLoginUrl);
            }
        } else {
            super.commence(request, response, authException);
        }
    }
}
