package org.truenewx.tnxjee.web.security.access;

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
import org.truenewx.tnxjee.model.spec.user.security.GrantedPermissionAuthority;
import org.truenewx.tnxjee.model.spec.user.security.GrantedRoleAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.NoAccessAuthority;
import org.truenewx.tnxjee.web.util.WebUtil;

import java.util.Arrays;
import java.util.Collection;

/**
 * 基于用户权限的访问判定管理器
 */
public class UserAuthorityAccessDecisionManager extends UnanimousBased implements GrantedAuthorityDecider {

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
            if (configAuthority.isDenyAll()) {
                return false;
            }
            if (configAuthority.isIntranet()) { // 如果限制内网访问
                String ip = WebUtil.getRemoteAddress(fi.getHttpRequest());
                if (!NetUtil.isIntranetIp(ip)) {
                    return false; // 拒绝非内网访问
                }
            }
            return isGranted(authorities, configAuthority.getRole(), configAuthority.getPermission());
        }
        return true; // 不支持的配置权限视为匹配
    }

    @Override
    public boolean isGranted(Collection<? extends GrantedAuthority> authorities, String role, String permission) {
        return isGrantedRole(authorities, role) && isGrantedPermission(authorities, permission);
    }

    private boolean isGrantedRole(Collection<? extends GrantedAuthority> authorities, String role) {
        if (StringUtils.isBlank(role)) { // 配置权限不限制角色，则视为匹配包含
            return true;
        }
        for (GrantedAuthority authority : authorities) {
            if (role.equals(authority.getAuthority()) || role.equals(authority.toString())) {
                return true;
            }
            if (authority instanceof GrantedRoleAuthority) {
                GrantedRoleAuthority roleAuthority = (GrantedRoleAuthority) authority;
                if (role.equals(roleAuthority.getRole())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGrantedPermission(Collection<? extends GrantedAuthority> authorities, String permission) {
        if (StringUtils.isBlank(permission)) { // 配置权限不限制许可，则视为匹配包含
            return true;
        }
        for (GrantedAuthority authority : authorities) {
            if (permission.equals(authority.getAuthority()) || permission.equals(authority.toString())) {
                return true;
            }
            if (authority instanceof GrantedPermissionAuthority) {
                GrantedPermissionAuthority permissionAuthority = (GrantedPermissionAuthority) authority;
                if (permission.equals(permissionAuthority.getPermission())) {
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
