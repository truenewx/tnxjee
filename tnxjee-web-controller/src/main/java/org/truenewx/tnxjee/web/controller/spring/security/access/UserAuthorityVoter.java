package org.truenewx.tnxjee.web.controller.spring.security.access;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.model.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.web.controller.spring.security.authentication.UserSpecificDetailsAuthenticationToken;
import org.truenewx.tnxjee.web.controller.util.WebControllerUtil;

import java.util.Collection;

/**
 * 用户权限投票者
 */
public class UserAuthorityVoter implements AccessDecisionVoter<FilterInvocation> {

    @Override
    public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {
        if (authentication instanceof UserSpecificDetailsAuthenticationToken) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (contains(fi, authorities, attributes)) {
                return ACCESS_GRANTED; // 同意
            }
            return ACCESS_DENIED; // 拒绝
        }
        return ACCESS_ABSTAIN; // 弃权
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
            UserConfigAuthority userAttribute = (UserConfigAuthority) attribute;
            if (userAttribute.isIntranet()) { // 如果限制内网访问
                String ip = WebControllerUtil.getRemoteAddress(fi.getHttpRequest());
                if (!NetUtil.isIntranetIp(ip)) {
                    return false; // 拒绝非内网访问
                }
            }
            if (userAttribute.isAnonymous()) { // 允许匿名访问则直接返回true
                return true;
            }
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(userAttribute.getAttribute())) { // 找到一个匹配则说明包含
                    return true;
                }
            }
            // 没有找到匹配则说明不包含
            return false;
        }
        return true; // 不支持的配置权限视为匹配
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
