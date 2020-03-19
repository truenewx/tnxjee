package org.truenewx.tnxjee.web.security.web.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.web.util.SpringWebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * WEB未登录访问限制的进入点
 */
public class WebAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public WebAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        try {
            HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
            if (handlerMethod != null) {
                if (SpringWebUtil.isResponseBody(handlerMethod)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 未授权错误
                } else {
                    super.commence(request, response, authException);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
