package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.exception.message.ResolvableExceptionMessageSaver;

/**
 * 基于可解决异常的登录认证失败处理器
 */
@Component
public class ResolvableExceptionAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private boolean useForward = true;
    private Function<HttpServletRequest, String> targetUrlFunction = request -> null;
    @Autowired
    private ResolvableExceptionMessageSaver resolvableExceptionMessageSaver;
    @Autowired
    private WebMvcProperties webMvcProperties;

    public void setUseForward(boolean useForward) {
        this.useForward = useForward;
    }

    public void setTargetUrlFunction(Function<HttpServletRequest, String> targetUrlFunction) {
        this.targetUrlFunction = targetUrlFunction;
    }

    public Function<HttpServletRequest, String> getTargetUrlFunction() {
        return this.targetUrlFunction;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        saveException(request, response, exception);

        // AJAX请求登录认证失败直接报401错误
        if (WebUtil.isAjaxRequest(request)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        } else {
            String targetUrl =
                    this.targetUrlFunction == null ? null : this.targetUrlFunction.apply(request);
            if (StringUtils.isBlank(targetUrl)) { // 登录认证失败后的跳转地址未设置，也报401错误
                response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            } else { // 跳转到目标地址
                if (this.useForward) {
                    WebMvcProperties.View view = this.webMvcProperties.getView();
                    targetUrl = view.getPrefix() + targetUrl + view.getSuffix();
                    request.getRequestDispatcher(targetUrl).forward(request, response);
                } else {
                    response.sendRedirect(targetUrl);
                }
            }
        }
    }

    protected void saveException(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof ResolvableException) {
            ResolvableException re = (ResolvableException) cause;
            this.resolvableExceptionMessageSaver.saveMessage(request, response, null, re);
        }
    }

}
