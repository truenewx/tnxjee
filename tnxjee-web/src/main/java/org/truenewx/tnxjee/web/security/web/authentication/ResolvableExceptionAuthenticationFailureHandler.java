package org.truenewx.tnxjee.web.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.exception.message.ResolvableExceptionMessageSaver;
import org.truenewx.tnxjee.web.security.core.AuthenticationFailureException;

/**
 * 基于可解决异常的认证失败处理器
 */
@Component
public class ResolvableExceptionAuthenticationFailureHandler
        extends SimpleUrlAuthenticationFailureHandler implements
        ApplicationContextAware {

    private ResolvableExceptionMessageSaver resolvableExceptionMessageSaver;

    public ResolvableExceptionAuthenticationFailureHandler() {
        setDefaultFailureUrl("/error/business");
        setUseForward(true);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.resolvableExceptionMessageSaver = context.getBean(ResolvableExceptionMessageSaver.class);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        try {
            ResolvableException re = null;
            if (exception instanceof AuthenticationFailureException) {
                re = new BusinessException(exception.getMessage());
            } else {
                Throwable cause = exception.getCause();
                if (cause instanceof ResolvableException) {
                    re = (ResolvableException) cause;
                }
            }
            if (re != null) {
                this.resolvableExceptionMessageSaver.saveMessage(request, response, re);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        // 保存业务异常后，执行默认的处理机制
        super.onAuthenticationFailure(request, response, exception);
    }

}
