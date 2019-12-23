package org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.truenewx.tnxjee.web.controller.spring.web.servlet.RequestHandlerSource;

/**
 * WEB过滤器调用安全元数据源
 */
public class WebFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private RequestHandlerSource handlerSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        try {
            HandlerExecutionChain chain = this.handlerSource.getHandler(fi.getRequest());
            if (chain != null) {
                Object handler = chain.getHandler();
                if (handler instanceof HandlerMethod) {
                    HandlerMethod method = (HandlerMethod) handler;

                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }
}
