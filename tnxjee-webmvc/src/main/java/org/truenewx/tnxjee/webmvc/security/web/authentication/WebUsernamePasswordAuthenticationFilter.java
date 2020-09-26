package org.truenewx.tnxjee.webmvc.security.web.authentication;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.servlet.mvc.LoginUrlResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

/**
 * WEB站点基于用户名密码的鉴权过滤器
 */
public class WebUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public WebUsernamePasswordAuthenticationFilter(ApplicationContext context) {
        ApiMetaProperties apiMetaProperties = SpringUtil.getFirstBeanByClass(context, ApiMetaProperties.class);
        if (apiMetaProperties != null) {
            String successTargetUrlParameter = apiMetaProperties.getLoginSuccessRedirectParameter();
            if (StringUtils.isNotBlank(successTargetUrlParameter)) {
                setSuccessTargetUrlParameter(successTargetUrlParameter);
            }
        }
        ResolvableExceptionAuthenticationFailureHandler failureHandler = SpringUtil.getFirstBeanByClass(context, ResolvableExceptionAuthenticationFailureHandler.class);
        if (failureHandler != null) {
            setAuthenticationFailureHandler(failureHandler); // 指定登录失败时的处理器
        }
        LoginUrlResolver loginUrlResolver = SpringUtil.getFirstBeanByClass(context, LoginUrlResolver.class);
        if (loginUrlResolver != null) {
            setFailureTargetUrlFunction(request -> {
                return loginUrlResolver.getLoginFormUrl();
            });
        }
    }

    public void setSuccessTargetUrlParameter(String successTargetUrlParameter) {
        AuthenticationSuccessHandler successHandler = getSuccessHandler();
        if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
            AbstractAuthenticationTargetUrlRequestHandler filter = (AbstractAuthenticationTargetUrlRequestHandler) successHandler;
            filter.setTargetUrlParameter(successTargetUrlParameter);
        }
    }

    public void setFailureTargetUrlFunction(Function<HttpServletRequest, String> targetUrlFunction) {
        AuthenticationFailureHandler failureHandler = getFailureHandler();
        if (failureHandler instanceof ResolvableExceptionAuthenticationFailureHandler) {
            ResolvableExceptionAuthenticationFailureHandler filter = (ResolvableExceptionAuthenticationFailureHandler) failureHandler;
            filter.setTargetUrlFunction(targetUrlFunction);
        }
    }

}
