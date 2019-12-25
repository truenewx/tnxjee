package org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.model.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.web.controller.spring.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.web.controller.spring.web.servlet.WebRequestHandlerSource;

/**
 * WEB过滤器调用安全元数据源
 */
public class WebFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource,
        ApplicationContextAware {

    private FilterInvocationSecurityMetadataSource origin;
    @Autowired
    private WebRequestHandlerSource requestHandlerSource;

    private Map<Method, Collection<ConfigAttribute>> methodAttributesMap = new HashMap<>();

    public void setOrigin(FilterInvocationSecurityMetadataSource origin) {
        this.origin = origin;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Collection<Object> controllers = context.getBeansWithAnnotation(Controller.class).values();
        controllers.forEach(controller -> {
            Method[] methods = controller.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())) {
                    Collection<ConfigAttribute> configAttributes = getConfigAttributes(method);
                    if (configAttributes.size() > 0) {
                        this.methodAttributesMap.put(method, configAttributes);
                    }
                }
            }
        });
    }

    private Collection<ConfigAttribute> getConfigAttributes(Method method) {
        Collection<ConfigAttribute> attributes = new ArrayList<>();
        ConfigAuthority[] configAuthorities = method.getAnnotationsByType(ConfigAuthority.class);
        for (ConfigAuthority configAuthority : configAuthorities) {
            boolean intranet = configAuthority.intranet();
            if (configAuthority.anonymous()) {
                attributes.add(UserConfigAuthority.ofAnonymous(intranet));
            } else {
                attributes.add(new UserConfigAuthority(configAuthority.role(), configAuthority.permission(), intranet));
            }
        }
        return attributes;
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
        if (attributes != null) { // 从原始元数据源中取得登录限定，才考虑配置权限
            FilterInvocation fi = (FilterInvocation) object;
            try {
                HandlerMethod handlerMethod = this.requestHandlerSource.getHandlerMethod(fi.getRequest());
                if (handlerMethod != null) {
                    Method method = handlerMethod.getMethod();
                    Collection<ConfigAttribute> methodAttributes = this.methodAttributesMap.get(method);
                    if (methodAttributes != null) {
                        attributes.addAll(methodAttributes);
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return attributes;
    }

}
