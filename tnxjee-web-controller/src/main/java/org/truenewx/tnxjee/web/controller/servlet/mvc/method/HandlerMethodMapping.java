package org.truenewx.tnxjee.web.controller.servlet.mvc.method;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.truenewx.tnxjee.web.controller.http.HttpAction;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 请求处理方法映射
 */
public interface HandlerMethodMapping {

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

    Map<HttpAction, HandlerMethod> getAllHandlerMethods();
}
