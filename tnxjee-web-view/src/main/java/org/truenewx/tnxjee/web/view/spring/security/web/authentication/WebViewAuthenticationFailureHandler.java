package org.truenewx.tnxjee.web.view.spring.security.web.authentication;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.service.api.exception.ResolvableException;
import org.truenewx.tnxjee.web.controller.exception.message.BusinessExceptionMessageSaver;
import org.truenewx.tnxjee.web.controller.spring.web.servlet.WebRequestHandlerSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * WEB视图层认证失败处理器
 */
public class WebViewAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler
        implements ApplicationContextAware {

    private BusinessExceptionMessageSaver businessExceptionMessageSaver;
    private WebRequestHandlerSource requestHandlerSource;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.businessExceptionMessageSaver = context.getBean(BusinessExceptionMessageSaver.class);
        this.requestHandlerSource = context.getBean(WebRequestHandlerSource.class);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        Throwable cause = exception.getCause();
        if (cause instanceof ResolvableException) {
            ResolvableException re = (ResolvableException) cause;
            try {
                HandlerMethod handlerMethod = this.requestHandlerSource.getHandlerMethod(request);
                if (handlerMethod != null) {
                    this.businessExceptionMessageSaver.saveMessage(request, response, handlerMethod, re);
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
        super.onAuthenticationFailure(request, response, exception);
    }

}
