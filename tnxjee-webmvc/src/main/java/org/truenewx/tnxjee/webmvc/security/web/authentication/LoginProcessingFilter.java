package org.truenewx.tnxjee.webmvc.security.web.authentication;

import javax.servlet.Filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.servlet.mvc.LoginUrlResolver;

/**
 * 登录进程过滤器
 */
public interface LoginProcessingFilter extends Filter {

    AuthenticationSuccessHandler getSuccessHandler();

    AuthenticationFailureHandler getFailureHandler();

    static void init(LoginProcessingFilter filter, ApplicationContext context) {
        ApiMetaProperties apiMetaProperties = SpringUtil.getFirstBeanByClass(context, ApiMetaProperties.class);
        if (apiMetaProperties != null) {
            String successTargetUrlParameter = apiMetaProperties.getLoginSuccessRedirectParameter();
            if (StringUtils.isNotBlank(successTargetUrlParameter)) {
                AuthenticationSuccessHandler successHandler = filter.getSuccessHandler();
                if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
                    ((AbstractAuthenticationTargetUrlRequestHandler) successHandler)
                            .setTargetUrlParameter(successTargetUrlParameter);
                }
            }
        }
        if (filter instanceof AbstractAuthenticationProcessingFilter) {
            ResolvableExceptionAuthenticationFailureHandler failureHandler = SpringUtil
                    .getFirstBeanByClass(context, ResolvableExceptionAuthenticationFailureHandler.class);
            if (failureHandler != null) {
                LoginUrlResolver loginUrlResolver = SpringUtil.getFirstBeanByClass(context, LoginUrlResolver.class);
                if (loginUrlResolver != null) {
                    failureHandler.setTargetUrlFunction(request -> loginUrlResolver.getLoginFormUrl());
                }
                ((AbstractAuthenticationProcessingFilter) filter)
                        .setAuthenticationFailureHandler(failureHandler); // 指定登录失败时的处理器
            }
        }
    }

}
