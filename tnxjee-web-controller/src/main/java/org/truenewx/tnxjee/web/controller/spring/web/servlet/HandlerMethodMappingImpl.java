package org.truenewx.tnxjee.web.controller.spring.web.servlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.truenewx.tnxjee.web.controller.http.HttpAction;

/**
 * 请求处理方法映射实现
 */
@Component
public class HandlerMethodMappingImpl implements HandlerMethodMapping {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    private Map<HttpAction, HandlerMethod> handlerMethods;

    @Override
    public HandlerExecutionChain getHandlerChain(HttpServletRequest request) throws Exception {
        return this.handlerMapping.getHandler(request);
    }

    @Override
    public Map<HttpAction, HandlerMethod> getAllHandlerMethods() {
        if (this.handlerMethods == null) {
            this.handlerMethods = new HashMap<>();
            this.handlerMapping.getHandlerMethods().forEach((request, handlerMethod) -> {
                request.getPatternsCondition().getPatterns().forEach(pattern -> {
                    Set<RequestMethod> requestMethods = request.getMethodsCondition().getMethods();
                    if (requestMethods.isEmpty()) {
                        HttpAction action = new HttpAction(pattern);
                        this.handlerMethods.put(action, handlerMethod);
                    } else {
                        requestMethods.forEach(requestMethod -> {
                            HttpAction action = new HttpAction(pattern, requestMethod);
                            this.handlerMethods.put(action, handlerMethod);
                        });
                    }
                });
            });
        }
        return Collections.unmodifiableMap(this.handlerMethods);
    }

}
