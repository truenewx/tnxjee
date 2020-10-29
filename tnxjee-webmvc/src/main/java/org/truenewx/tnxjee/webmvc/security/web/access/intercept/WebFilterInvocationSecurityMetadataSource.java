package org.truenewx.tnxjee.webmvc.security.web.access.intercept;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigPermission;
import org.truenewx.tnxjee.webmvc.security.web.access.ConfigAuthorityResolver;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * WEB过滤器调用安全元数据源<br>
 * 用于获取访问资源需要具备的权限
 */
public class WebFilterInvocationSecurityMetadataSource implements
        FilterInvocationSecurityMetadataSource, ContextInitializedBean, ConfigAuthorityResolver {

    private FilterInvocationSecurityMetadataSource origin;
    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    private final Map<String, ConfigAttribute> configAttributeMap = new HashMap<>();
    private String permissionPrefix = Strings.EMPTY;

    public void setOrigin(FilterInvocationSecurityMetadataSource origin) {
        this.origin = origin;
    }

    @Override
    public void afterInitialized(ApplicationContext context) {
        // 存在多个微服务应用，则许可名称前需添加当前应用前缀
        CommonProperties commonProperties = SpringUtil.getFirstBeanByClass(context, CommonProperties.class);
        if (commonProperties != null && commonProperties.getApps().size() > 1) {
            String appName = context.getEnvironment().getProperty("spring.application.name");
            if (StringUtils.isNotBlank(appName)) {
                this.permissionPrefix = appName + Strings.DOT;
            }
        }
        this.handlerMethodMapping.getAllHandlerMethods().forEach((action, handlerMethod) -> {
            UserConfigAuthority userConfigAuthority = getUserConfigAuthority(handlerMethod);
            if (userConfigAuthority != null) {
                String methodKey = handlerMethod.getMethod().toString();
                this.configAttributeMap.put(methodKey, userConfigAuthority);
            }
        });
    }

    private UserConfigAuthority getUserConfigAuthority(HandlerMethod handlerMethod) {
        // 允许匿名访问，则忽略权限限定
        if (handlerMethod.getMethodAnnotation(ConfigAnonymous.class) != null) {
            return null;
        }

        String url = SpringWebMvcUtil.getRequestMappingUrl(handlerMethod);
        UserConfigAuthority authority = null;
        ConfigAuthority configAuthority = handlerMethod.getMethodAnnotation(ConfigAuthority.class);
        if (configAuthority != null) {
            String permission = withPermissionPrefix(configAuthority.permission());
            authority = new UserConfigAuthority(configAuthority.type(), configAuthority.rank(),
                    permission, configAuthority.intranet());
        } else { // 没有@ConfigAuthority则考虑@ConfigPermission
            ConfigPermission configPermission = handlerMethod.getMethodAnnotation(ConfigPermission.class);
            if (configPermission != null) {
                String permission = withPermissionPrefix(getDefaultPermission(url));
                authority = new UserConfigAuthority(configPermission.type(), configPermission.rank(),
                        permission, configPermission.intranet());
            }
        }
        if (authority == null) { // 没有配置权限限定，则拒绝所有访问
            authority = UserConfigAuthority.ofDenyAll();
        } else {
            LogUtil.info(getClass(), "Config authority: {} => {}", url, authority);
        }
        return authority;
    }

    private String getDefaultPermission(String url) {
        // 确保头尾都有/
        url = StringUtils.wrapIfMissing(url, Strings.SLASH);
        // 移除可能包含的路径变量
        if (url.endsWith("/{id}/")) { // 以路径变量id结尾的，默认视为detail
            url = url.replaceAll("/\\{id\\}/", "/detail/");
        }
        url = url.replaceAll("/\\{[^}]*\\}/", Strings.SLASH);
        // 去掉头尾的/
        url = StringUtils.strip(url, Strings.SLASH);
        // 替换中间的/为.
        return url.replaceAll(Strings.SLASH, Strings.DOT);
    }

    private String withPermissionPrefix(String permission) {
        return StringUtils.isBlank(permission) ? Strings.EMPTY : (this.permissionPrefix + permission);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return this.configAttributeMap.values();
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object)
            throws IllegalArgumentException {
        Collection<ConfigAttribute> attributes = null;
        if (this.origin != null) {
            attributes = this.origin.getAttributes(object);
        }
        if (supports(attributes)) {
            attributes = attributes == null ? new HashSet<>() : new HashSet<>(attributes);
            FilterInvocation fi = (FilterInvocation) object;
            try {
                HandlerMethod handlerMethod = this.handlerMethodMapping
                        .getHandlerMethod(fi.getRequest());
                if (handlerMethod != null) {
                    String methodKey = handlerMethod.getMethod().toString();
                    ConfigAttribute userConfigAuthority = this.configAttributeMap.get(methodKey);
                    if (userConfigAuthority == null) { // 加入一个没有权限限制的必备权限，以标记进行过处理
                        attributes.add(new UserConfigAuthority());
                    } else {
                        attributes.add(userConfigAuthority);
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
            if ("permitAll".equals(attribute.getAttribute())
                    || "permitAll".equals(attribute.toString())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public UserConfigAuthority resolveConfigAuthority(String uri, HttpMethod method) {
        HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(uri, method);
        if (handlerMethod == null) {
            LogUtil.warn(getClass(), "There is not handlerMethod for {}->{}", method.name(), uri);
            return null;
        }
        String methodKey = handlerMethod.getMethod().toString();
        return (UserConfigAuthority) this.configAttributeMap.get(methodKey);
    }

}
