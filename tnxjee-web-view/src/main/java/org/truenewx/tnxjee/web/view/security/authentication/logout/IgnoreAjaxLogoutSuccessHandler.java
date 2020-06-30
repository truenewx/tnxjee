package org.truenewx.tnxjee.web.view.security.authentication.logout;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;

/**
 * 忽略AJAX请求的登出成功处理器
 */
public class IgnoreAjaxLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // AJAX登出成功什么都不做，非AJAX登出成功才做正常处理
        if (!this.handlerMethodMapping.isAjaxRequest(request)) {
            super.handle(request, response, authentication);
        }
    }

}
