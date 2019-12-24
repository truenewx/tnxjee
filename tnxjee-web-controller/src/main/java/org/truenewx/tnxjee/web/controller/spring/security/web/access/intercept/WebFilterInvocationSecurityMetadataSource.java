package org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.web.controller.spring.web.servlet.WebRequestHandlerSource;

/**
 * WEB过滤器调用安全元数据源
 */
public class WebFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private FilterInvocationSecurityMetadataSource origin;
    @Autowired
    private WebRequestHandlerSource requestHandlerSource;

    public void setOrigin(FilterInvocationSecurityMetadataSource origin) {
        this.origin = origin;
    }

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
        Collection<ConfigAttribute> attributes = null;
        if (this.origin != null) {
            attributes = this.origin.getAttributes(object);
        }
        FilterInvocation fi = (FilterInvocation) object;
        try {
            HandlerMethod handlerMethod = this.requestHandlerSource.getHandlerMethod(fi.getRequest());

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return attributes;
    }
}
