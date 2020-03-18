package org.truenewx.tnxjee.web.controller.security.web.access.intercept;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.web.controller.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.web.controller.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.web.controller.security.web.access.ConfigAuthorityResolver;
import org.truenewx.tnxjee.web.controller.servlet.mvc.method.HandlerMethodMapping;

import java.lang.reflect.Method;
import java.util.*;

/**
 * WEB过滤器调用安全元数据源<br>
 * 用于获取访问资源需要具备的权限
 */
public class WebFilterInvocationSecurityMetadataSource
        implements FilterInvocationSecurityMetadataSource, ContextInitializedBean, ConfigAuthorityResolver {

    private FilterInvocationSecurityMetadataSource origin;
    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    private Map<String, Collection<UserConfigAuthority>> configAttributesMap = new HashMap<>();

    public void setOrigin(FilterInvocationSecurityMetadataSource origin) {
        this.origin = origin;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        this.handlerMethodMapping.getAllHandlerMethods().forEach((action, handlerMethod) -> {
            Method method = handlerMethod.getMethod();
            Collection<UserConfigAuthority> userConfigAuthorities = getConfigAttributes(method);
            if (userConfigAuthorities != null && userConfigAuthorities.size() > 0) {
                this.configAttributesMap.put(method.toString(), userConfigAuthorities);
            }
        });
    }

    private Collection<UserConfigAuthority> getConfigAttributes(Method method) {
        if (method.getAnnotation(ConfigAnonymous.class) != null) { // 允许匿名访问，则忽略权限限定
            return null;
        }
        Collection<UserConfigAuthority> userConfigAuthorities = new ArrayList<>();
        ConfigAuthority[] configAuthorities = method.getAnnotationsByType(ConfigAuthority.class);
        if (configAuthorities.length == 0) { // 没有配置权限限定，则拒绝所有访问
            userConfigAuthorities.add(UserConfigAuthority.ofDenyAll());
        } else {
            for (ConfigAuthority configAuthority : configAuthorities) {
                UserConfigAuthority userConfigAuthority = new UserConfigAuthority(configAuthority.role(),
                        configAuthority.permission(), configAuthority.intranet());
                userConfigAuthorities.add(userConfigAuthority);
            }
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
                HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(fi.getRequest());
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

    @Override
    public Collection<UserConfigAuthority> resolveConfigAuthorities(String uri, HttpMethod method) {
        HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(uri, method);
        if (handlerMethod == null) {
            LogUtil.warn(getClass(), "There is not handlerMethod for {}->{}", method.name(), uri);
            return null;
        }
        String methodKey = handlerMethod.getMethod().toString();
        return this.configAttributesMap.get(methodKey);
    }

}
