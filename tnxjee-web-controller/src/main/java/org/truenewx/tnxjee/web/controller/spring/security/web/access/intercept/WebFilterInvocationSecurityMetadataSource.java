package org.truenewx.tnxjee.web.controller.spring.security.web.access.intercept;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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
public class WebFilterInvocationSecurityMetadataSource
        implements FilterInvocationSecurityMetadataSource, ApplicationContextAware {

    private FilterInvocationSecurityMetadataSource origin;
    @Autowired
    private WebRequestHandlerSource requestHandlerSource;

    private Map<String, Collection<UserConfigAuthority>> configAttributesMap = new HashMap<>();

    public void setOrigin(FilterInvocationSecurityMetadataSource origin) {
        this.origin = origin;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Collection<Object> controllers = context.getBeansWithAnnotation(Controller.class).values();
        controllers.forEach(controller -> {
            Method[] methods = controller.getClass().getMethods();
            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())) {
                    Collection<UserConfigAuthority> userConfigAuthorities = getConfigAttributes(method);
                    if (userConfigAuthorities.size() > 0) {
                        this.configAttributesMap.put(method.toString(), userConfigAuthorities);
                    }
                }
            }
        });
    }

    private Collection<UserConfigAuthority> getConfigAttributes(Method method) {
        Collection<UserConfigAuthority> userConfigAuthorities = new ArrayList<>();
        ConfigAuthority[] configAuthorities = method.getAnnotationsByType(ConfigAuthority.class);
        for (ConfigAuthority configAuthority : configAuthorities) {
            UserConfigAuthority userConfigAuthority = new UserConfigAuthority(configAuthority.role(),
                    configAuthority.permission(), configAuthority.intranet());
            userConfigAuthorities.add(userConfigAuthority);
        }
        return userConfigAuthorities;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Collection<ConfigAttribute> result = new HashSet<>();
        this.configAttributesMap.values().forEach(attributes -> {
            result.addAll(attributes);
        });
        return result;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        Collection<ConfigAttribute> attributes = null;
        if (this.origin != null) {
            attributes = this.origin.getAttributes(object);
        }
        if (supports(attributes)) {
            attributes = new HashSet<>(attributes);
            FilterInvocation fi = (FilterInvocation) object;
            try {
                HandlerMethod handlerMethod = this.requestHandlerSource.getHandlerMethod(fi.getRequest());
                if (handlerMethod != null) {
                    String methodKey = handlerMethod.getMethod().toString();
                    Collection<UserConfigAuthority> userConfigAuthorities = this.configAttributesMap.get(methodKey);
                    if (userConfigAuthorities == null) { // 加入一个没有权限限制的必备权限，以标记进行过处理
                        attributes.add(new UserConfigAuthority());
                    } else {
                        attributes.addAll(userConfigAuthorities);
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return attributes;
    }

    private boolean supports(Collection<ConfigAttribute> originalAttributes) {
        // 原始配置属性为空，一定不是登录才能访问，不支持
        if (CollectionUtils.isEmpty(originalAttributes)) {
            return false;
        }
        for (ConfigAttribute attribute : originalAttributes) {
            // 原始配置属性包含不限制访问，说明也不是登录才能访问，不支持
            if ("permitAll".equals(attribute.getAttribute()) || "permitAll".equals(attribute.toString())) {
                return false;
            }
        }
        return true;
    }

}
