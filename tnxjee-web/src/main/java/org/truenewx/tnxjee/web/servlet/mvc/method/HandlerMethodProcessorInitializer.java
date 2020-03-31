package org.truenewx.tnxjee.web.servlet.mvc.method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理器方法处理器初始化器，负责将自定义的方法处理器加载到容器中
 */
@Component
public class HandlerMethodProcessorInitializer {

    @Autowired
    public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter adapter) {
        EscapeRequestResponseBodyMethodProcessor bodyProcessor = null;

        List<HandlerMethodArgumentResolver> resolvers = adapter.getArgumentResolvers();
        if (resolvers != null) {
            List<HandlerMethodArgumentResolver> delegatedResolvers = new ArrayList<>();
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                if (resolver instanceof RequestResponseBodyMethodProcessor) {
                    bodyProcessor = new EscapeRequestResponseBodyMethodProcessor((RequestResponseBodyMethodProcessor) resolver);
                    delegatedResolvers.add(bodyProcessor);
                } else {
                    delegatedResolvers.add(resolver);
                }
            }
            adapter.setArgumentResolvers(delegatedResolvers);
        }

        List<HandlerMethodReturnValueHandler> handlers = adapter.getReturnValueHandlers();
        if (handlers != null) {
            List<HandlerMethodReturnValueHandler> delegatedHandlers = new ArrayList<>();
            for (HandlerMethodReturnValueHandler handler : handlers) {
                if (handler instanceof RequestResponseBodyMethodProcessor) {
                    delegatedHandlers.add(bodyProcessor);
                } else {
                    delegatedHandlers.add(handler);
                }
            }
            adapter.setReturnValueHandlers(delegatedHandlers);
        }
    }

}
