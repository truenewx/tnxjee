package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjee.webmvc.util.RpcUtil;

/**
 * WEB未登录访问限制的进入点
 */
public class WebAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired
    private RedirectStrategy redirectStrategy;
    @Autowired(required = false)
    private SecurityUrlProvider securityUrlProvider;
    private boolean ajaxToForm;

    public WebAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        if (this.securityUrlProvider != null) {
            return this.securityUrlProvider.getLoginFormUrl(request);
        }
        return super.determineUrlToUseForThisRequest(request, response, exception);
    }

    /**
     * 设置是否将AJAX请求直接跳转到登录表单页
     *
     * @param ajaxToForm 是否将AJAX请求直接跳转到登录表单页
     */
    public void setAjaxToForm(boolean ajaxToForm) {
        this.ajaxToForm = ajaxToForm;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (RpcUtil.isInternalRpc(request)) { // 内部RPC调用直接返回401错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (WebUtil.isAjaxRequest(request)) { // AJAX请求执行特殊的跳转
            String redirectLoginUrl = buildRedirectUrlToLoginPage(request, response, authException);
            if (this.ajaxToForm) { // AJAX请求直接跳转到登录表单页
                response.setHeader(WebConstants.HEADER_LOGIN_URL, redirectLoginUrl);
            } else {
                this.redirectStrategy.sendRedirect(request, response, redirectLoginUrl);
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        super.commence(request, response, authException);
    }
}
