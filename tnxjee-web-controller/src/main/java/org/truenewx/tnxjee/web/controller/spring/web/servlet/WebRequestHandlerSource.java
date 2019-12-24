package org.truenewx.tnxjee.web.controller.spring.web.servlet;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;

import javax.servlet.http.HttpServletRequest;

/**
 * WEB请求处理器源
 */
public interface WebRequestHandlerSource {

    HandlerExecutionChain getHandlerChain(HttpServletRequest request) throws Exception;

    default HandlerMethod getHandlerMethod(HttpServletRequest request) throws Exception {
        HandlerExecutionChain chain = getHandlerChain(request);
        if (chain != null) {
            Object handler = chain.getHandler();
            if (handler instanceof HandlerMethod) {
                return (HandlerMethod) handler;
            }
        }
        return null;
    }

}
