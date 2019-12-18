package org.truenewx.tnxjee.web.controller.spring.security.access;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;

/**
 * WEB访问判定管理器
 */
public class WebAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        if (CollectionUtils.isNotEmpty(configAttributes)) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (matches(authorities, configAttributes)) {
                return;
            }
        }
        throw new AccessDeniedException("NoAuthority");
    }

    private boolean matches(Collection<? extends GrantedAuthority> authorities,
            Collection<ConfigAttribute> configAttributes) {
        for (ConfigAttribute attribute : configAttributes) {
            if (!matches(authorities, attribute)) {
                return false;
            }
        }
        return true;
    }

    private boolean matches(Collection<? extends GrantedAuthority> authorities, ConfigAttribute attribute) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().trim().equals(attribute.getAttribute().trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
