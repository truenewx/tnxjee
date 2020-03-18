package org.truenewx.tnxjee.web.controller.security.web.authentication;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.truenewx.tnxjee.web.controller.exception.message.BusinessCauseMessageSaver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 基于业务异常的认证失败处理器
 */
public class BusinessExceptionAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler implements
        ApplicationContextAware {

    private BusinessCauseMessageSaver businessCauseMessageSaver;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.businessCauseMessageSaver = context.getBean(BusinessCauseMessageSaver.class);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        try {
            this.businessCauseMessageSaver.saveMessage(request, response, exception);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        // 保存业务异常后，执行默认的处理机制
        super.onAuthenticationFailure(request, response, exception);
    }

}
