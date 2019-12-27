package org.truenewx.tnxjee.web.controller.spring.security.access;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.model.user.security.GrantedPermissionAuthority;
import org.truenewx.tnxjee.model.user.security.GrantedRoleAuthority;
import org.truenewx.tnxjee.model.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.service.api.exception.BusinessException;
import org.truenewx.tnxjee.service.api.exception.NoAccessAuthority;
import org.truenewx.tnxjee.web.controller.util.WebControllerUtil;

/**
 * 基于用户权限的访问判定管理器
 */
public class UserAuthorityAccessDecisionManager extends UnanimousBased {

    public UserAuthorityAccessDecisionManager() {
        super(Arrays.asList(new WebExpressionVoter()));
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        super.decide(authentication, object, configAttributes);

        FilterInvocation fi = (FilterInvocation) object;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (!contains(fi, authorities, configAttributes)) {
            BusinessException be = new NoAccessAuthority();
            throw new AccessDeniedException(be.getLocalizedMessage(), be);
        }
    }

    private boolean contains(FilterInvocation fi, Collection<? extends GrantedAuthority> authorities,
            Collection<ConfigAttribute> configAttributes) {
        for (ConfigAttribute attribute : configAttributes) {
            if (!contains(fi, authorities, attribute)) { // 只要有一个要求的权限未包含，则不匹配
                return false;
            }
        }
        // 拥有的权限集包含所有要求的权限，才视为匹配
        return true;
    }

    private boolean contains(FilterInvocation fi, Collection<? extends GrantedAuthority> authorities,
            ConfigAttribute attribute) {
        if (supports(attribute)) {
            UserConfigAuthority configAuthority = (UserConfigAuthority) attribute;
            if (configAuthority.isIntranet()) { // 如果限制内网访问
                String ip = WebControllerUtil.getRemoteAddress(fi.getHttpRequest());
                if (!NetUtil.isIntranetIp(ip)) {
                    return false; // 拒绝非内网访问
                }
            }
            return containsRole(authorities, configAuthority.getRole())
                    && containsPermission(authorities, configAuthority.getPermission());
        }
        return true; // 不支持的配置权限视为匹配
    }

    private boolean containsRole(Collection<? extends GrantedAuthority> authorities, String configRole) {
        if (StringUtils.isBlank(configRole)) { // 配置权限不限制角色，则视为匹配包含
            return true;
        }
        for (GrantedAuthority authority : authorities) {
            if (configRole.equals(authority.getAuthority()) || configRole.equals(authority.toString())) {
                return true;
            }
            if (authority instanceof GrantedRoleAuthority) {
                GrantedRoleAuthority roleAuthority = (GrantedRoleAuthority) authority;
                if (configRole.equals(roleAuthority.getRole())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsPermission(Collection<? extends GrantedAuthority> authorities, String configPermission) {
        if (StringUtils.isBlank(configPermission)) { // 配置权限不限制许可，则视为匹配包含
            return true;
        }
        for (GrantedAuthority authority : authorities) {
            if (configPermission.equals(authority.getAuthority()) || configPermission.equals(authority.toString())) {
                return true;
            }
            if (authority instanceof GrantedPermissionAuthority) {
                GrantedPermissionAuthority permissionAuthority = (GrantedPermissionAuthority) authority;
                if (configPermission.equals(permissionAuthority.getPermission())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof UserConfigAuthority;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
