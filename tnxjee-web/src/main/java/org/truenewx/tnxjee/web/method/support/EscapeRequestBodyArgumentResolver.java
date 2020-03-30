package org.truenewx.tnxjee.web.method.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 转义@RequestBody参数的参数解决器
 */
@Component
public class EscapeRequestBodyArgumentResolver implements HandlerMethodArgumentResolver {

    private RequestResponseBodyMethodProcessor delegate;

    @Autowired
    public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter adapter) {
        List<HandlerMethodArgumentResolver> resolvers = adapter.getArgumentResolvers();
        if (resolvers != null) {
            List<HandlerMethodArgumentResolver> delegatedResolvers = new ArrayList<>();
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                if (resolver instanceof RequestResponseBodyMethodProcessor) {
                    this.delegate = (RequestResponseBodyMethodProcessor) resolver;
                    delegatedResolvers.add(this);
                } else {
                    delegatedResolvers.add(resolver);
                }
            }
            adapter.setArgumentResolvers(delegatedResolvers);
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return this.delegate != null && this.delegate.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object object = this.delegate.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (object != null) {

        }
        return object;
    }

}
