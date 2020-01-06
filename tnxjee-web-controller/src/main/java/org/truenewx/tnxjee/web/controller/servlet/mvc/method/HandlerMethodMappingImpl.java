package org.truenewx.tnxjee.web.controller.servlet.mvc.method;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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
        ensure();
        return Collections.unmodifiableMap(this.handlerMethods);
    }

    private void ensure() {
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
    }

    @Override
    public HandlerMethod getHandlerMethod(String uri, HttpMethod method) {
        ensure();
        HandlerMethod handlerMethod = this.handlerMethods.get(new HttpAction(uri, method));
        // 如果指定了访问方法无法取得，则尝试不指定访问方法获取，因为可能存在不限定访问方法的处理方法
        if (handlerMethod == null && method != null) {
            handlerMethod = this.handlerMethods.get(new HttpAction(uri));
        }
        return handlerMethod;
    }
}
