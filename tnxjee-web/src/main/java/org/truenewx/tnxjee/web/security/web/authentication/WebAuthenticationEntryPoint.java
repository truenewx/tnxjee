package org.truenewx.tnxjee.web.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.web.util.SpringWebUtil;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * WEB未登录访问限制的进入点
 */
public class WebAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    private String loginAjaxUrl;

    public WebAuthenticationEntryPoint(String loginFormUrl, String loginAjaxUrl) {
        super(loginFormUrl);
        this.loginAjaxUrl = loginAjaxUrl;
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception) {
        if (this.loginAjaxUrl != null && isAjaxRequest(request)) {
            return this.loginAjaxUrl;
        }
        return getLoginFormUrl();
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        if (WebUtil.isAjaxRequest(request)) {
            return true;
        }
        try {
            HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
            if (handlerMethod != null) {
                return SpringWebUtil.isResponseBody(handlerMethod);
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return false;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (isAjaxRequest(request)) {
            if (this.loginAjaxUrl != null) {
                String targetUrl = buildRedirectUrlToLoginPage(request, response, authException);
                response.setHeader("redirect", targetUrl);
            } else {  // AJAX请求没有跳转目标则返回未授权错误
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            super.commence(request, response, authException);
        }
    }
}
