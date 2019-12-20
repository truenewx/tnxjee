package org.truenewx.tnxjee.web.view.spring.security.web.authentication;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.spring.util.SpringUtil;
import org.truenewx.tnxjee.service.api.exception.BusinessException;
import org.truenewx.tnxjee.web.view.exception.resolver.ViewBusinessExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * WEB视图层鉴权失败处理器
 */
public class WebViewAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler implements
        ApplicationContextAware {

    private ViewBusinessExceptionResolver businessExceptionResolver;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.businessExceptionResolver = SpringUtil.getFirstBeanByClass(context, ViewBusinessExceptionResolver.class);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        Throwable cause = exception.getCause();
        if (cause instanceof BusinessException) {
            BusinessException be = (BusinessException) cause;
            this.businessExceptionResolver.saveException(request, response, be);
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
