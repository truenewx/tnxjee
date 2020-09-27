package org.truenewx.tnxjee.webmvc.security.access;

import java.util.Collection;
import java.util.Collections;

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
import org.truenewx.tnxjee.model.spec.user.security.GrantedAuthorityKind;
import org.truenewx.tnxjee.model.spec.user.security.KindGrantedAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.NoAccessAuthority;
import org.truenewx.tnxjee.webmvc.util.WebMvcUtil;

/**
 * 基于用户权限的访问判定管理器
 */
public class UserAuthorityAccessDecisionManager extends UnanimousBased
        implements GrantedAuthorityDecider {

    public UserAuthorityAccessDecisionManager() {
        super(Collections.singletonList(new WebExpressionVoter()));
    }

    @Override
    public void decide(Authentication authentication, Object object,
            Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        super.decide(authentication, object, configAttributes);

        FilterInvocation fi = (FilterInvocation) object;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (!contains(fi, authorities, configAttributes)) {
            BusinessException be = new NoAccessAuthority();
            throw new AccessDeniedException(be.getLocalizedMessage(), be);
        }
    }

    private boolean contains(FilterInvocation fi,
            Collection<? extends GrantedAuthority> authorities,
            Collection<ConfigAttribute> configAttributes) {
        for (ConfigAttribute attribute : configAttributes) {
            if (!contains(fi, authorities, attribute)) { // 只要有一个要求的权限未包含，则不匹配
                return false;
            }
        }
        // 拥有的权限集包含所有要求的权限，才视为匹配
        return true;
    }

    private boolean contains(FilterInvocation fi,
            Collection<? extends GrantedAuthority> authorities, ConfigAttribute attribute) {
        if (supports(attribute)) {
            UserConfigAuthority configAuthority = (UserConfigAuthority) attribute;
            if (configAuthority.isDenyAll()) {
                return false;
            }
            if (configAuthority.isIntranet()) { // 如果限制内网访问
                String ip = WebMvcUtil.getRemoteAddress(fi.getHttpRequest());
                if (!NetUtil.isIntranetIp(ip)) {
                    return false; // 拒绝非内网访问
                }
            }
            return isGranted(authorities, configAuthority.getType(), configAuthority.getRank(),
                    configAuthority.getPermission());
        }
        return true; // 不支持的配置权限视为匹配
    }

    @Override
    public boolean isGranted(Collection<? extends GrantedAuthority> authorities, String type,
            String rank, String permission) {
        return isKindGranted(authorities, GrantedAuthorityKind.TYPE, type)
                // 类型不为空才检查级别
                && (StringUtils.isBlank(type) || isKindGranted(authorities, GrantedAuthorityKind.RANK, rank))
                && isKindGranted(authorities, GrantedAuthorityKind.PERMISSION, permission);
    }

    private boolean isKindGranted(Collection<? extends GrantedAuthority> authorities,
            GrantedAuthorityKind kind, String name) {
        if (StringUtils.isBlank(name)) { // 配置权限不限制，则视为匹配包含
            return true;
        }
        for (GrantedAuthority authority : authorities) {
            if (authority instanceof KindGrantedAuthority) {
                KindGrantedAuthority kindAuthority = (KindGrantedAuthority) authority;
                if (kindAuthority.getKind() == kind && name.equals(kindAuthority.getName())) {
                    return true;
                }
            } else if (name.equals(authority.getAuthority()) || name.equals(authority.toString())) {
                return true;
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
