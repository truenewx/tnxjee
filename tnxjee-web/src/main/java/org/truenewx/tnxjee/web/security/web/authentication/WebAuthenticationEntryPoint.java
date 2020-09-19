package org.truenewx.tnxjee.web.security.web.authentication;

import java.io.IOException;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * WEB未登录访问限制的进入点
 */
public class WebAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired
    private RedirectStrategy redirectStrategy;
    private Function<String, Integer> responseStatusFunction = redirectUrl -> {
        // AJAX请求无法正确执行302跳转至开启CORS的目标地址，未登录时默认报401错误
        return HttpServletResponse.SC_UNAUTHORIZED;
    };
    private boolean ajaxToForm;

    public WebAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    public void setResponseStatusFunction(Function<String, Integer> responseStatusFunction) {
        this.responseStatusFunction = responseStatusFunction;
    }

    public void setAjaxToForm(boolean ajaxToForm) {
        this.ajaxToForm = ajaxToForm;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (WebUtil.isAjaxRequest(request)) {
            String redirectLoginUrl = buildRedirectUrlToLoginPage(request, response, authException);
            Integer status = this.responseStatusFunction == null ? null :
                    this.responseStatusFunction.apply(redirectLoginUrl);
            // 不是默认状态，则进行特殊的跳转
            if (status != null && status != HttpServletResponse.SC_FOUND) {
                if (this.ajaxToForm) {
                    response.setHeader(WebConstants.HEADER_LOGIN_URL, redirectLoginUrl);
                } else {
                    this.redirectStrategy.sendRedirect(request, response, redirectLoginUrl);
                }
                response.setStatus(status);
                return;
            }
        }
        super.commence(request, response, authException);
    }
}
