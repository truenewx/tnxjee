package org.truenewx.tnxjee.web.servlet.mvc.method;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.truenewx.tnxjee.web.http.HttpAction;
import org.truenewx.tnxjee.web.util.SpringWebUtil;
import org.truenewx.tnxjee.web.util.WebUtil;

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

    HandlerMethod getHandlerMethod(String uri, HttpMethod method);

    default boolean isAjaxRequest(HttpServletRequest request) {
        if (WebUtil.isAjaxRequest(request)) {
            return true;
        }
        try {
            HandlerMethod handlerMethod = getHandlerMethod(request);
            if (handlerMethod != null) {
                return SpringWebUtil.isResponseBody(handlerMethod);
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return false;
    }

}
