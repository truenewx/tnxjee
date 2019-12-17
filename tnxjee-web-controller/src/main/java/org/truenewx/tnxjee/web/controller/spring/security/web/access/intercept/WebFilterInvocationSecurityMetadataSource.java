package org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.truenewx.tnxjee.core.spring.util.SpringUtil;

import java.util.Collection;

/**
 * WEB过滤器调用安全元数据源
 */
public class WebFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource,
        ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
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
        FilterInvocation fi = (FilterInvocation) object;
        try {
            HandlerMapping handlerMapping = SpringUtil.getFirstBeanByClass(this.context, HandlerMapping.class);
            HandlerExecutionChain chain = handlerMapping.getHandler(fi.getRequest());
            Object handler = chain.getHandler();
            if (handler instanceof HandlerMethod) {
                HandlerMethod method = (HandlerMethod) handler;
                method.getMethodAnnotation(Secured.class);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }
}
