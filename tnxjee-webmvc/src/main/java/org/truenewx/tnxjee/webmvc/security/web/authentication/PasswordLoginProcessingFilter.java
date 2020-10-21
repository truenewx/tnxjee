package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.servlet.mvc.LoginUrlResolver;

/**
 * 密码登录进程过滤器
 */
public class PasswordLoginProcessingFilter extends UsernamePasswordAuthenticationFilter implements
        LoginProcessingHandlerAcceptable<AbstractAuthenticationTargetUrlRequestHandler, ResolvableExceptionAuthenticationFailureHandler> {

    public PasswordLoginProcessingFilter(ApplicationContext context) {
        ApiMetaProperties apiMetaProperties = SpringUtil.getFirstBeanByClass(context, ApiMetaProperties.class);
        if (apiMetaProperties != null) {
            String successTargetUrlParameter = apiMetaProperties.getLoginSuccessRedirectParameter();
            if (StringUtils.isNotBlank(successTargetUrlParameter)) {
                acceptSuccessHandler(handler -> {
                    handler.setTargetUrlParameter(successTargetUrlParameter);
                });
            }
        }
        ResolvableExceptionAuthenticationFailureHandler failureHandler = SpringUtil
                .getFirstBeanByClass(context, ResolvableExceptionAuthenticationFailureHandler.class);
        if (failureHandler != null) {
            LoginUrlResolver loginUrlResolver = SpringUtil.getFirstBeanByClass(context, LoginUrlResolver.class);
            if (loginUrlResolver != null) {
                failureHandler.setTargetUrlFunction(request -> loginUrlResolver.getLoginFormUrl());
            }
            setAuthenticationFailureHandler(failureHandler); // 指定登录失败时的处理器
        }
    }

    @Override
    public void acceptSuccessHandler(Consumer<AbstractAuthenticationTargetUrlRequestHandler> consumer) {
        AuthenticationSuccessHandler successHandler = getSuccessHandler();
        if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
            consumer.accept((AbstractAuthenticationTargetUrlRequestHandler) successHandler);
        }
    }

    @Override
    public void acceptFailureHandler(Consumer<ResolvableExceptionAuthenticationFailureHandler> consumer) {
        AuthenticationFailureHandler failureHandler = getFailureHandler();
        if (failureHandler instanceof ResolvableExceptionAuthenticationFailureHandler) {
            consumer.accept((ResolvableExceptionAuthenticationFailureHandler) failureHandler);
        }
    }

}
