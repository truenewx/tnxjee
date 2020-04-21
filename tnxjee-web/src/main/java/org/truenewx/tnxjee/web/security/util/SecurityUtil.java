package org.truenewx.tnxjee.web.security.util;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 安全工具类
 */
public class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * 获取已获得的授权清单，匿名用户也会拥有匿名的授权清单
     *
     * @return 已获得的授权清单
     */
    public static Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                return authentication.getAuthorities();
            }
        }
        return Collections.emptyList();
    }

    /**
     * 获取已授权的当前用户细节，匿名用户将返回null
     *
     * @param <I> 用户标识类型
     * @return 已授权的当前用户细节
     */
    @SuppressWarnings("unchecked")
    public static <I extends UserIdentity> UserSpecificDetails<I> getAuthorizedUserDetails() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                Object details = authentication.getDetails();
                if (details instanceof UserSpecificDetails) {
                    return (UserSpecificDetails<I>) details;
                }
            }
        }
        return null;
    }

    /**
     * 获取已授权的当前用户标识，匿名用户将返回null
     *
     * @param <I> 用户标识类型
     * @return 已授权的当前用户标识
     */
    public static <I extends UserIdentity> I getAuthorizedUserIdentity() {
        UserSpecificDetails<I> details = getAuthorizedUserDetails();
        return details == null ? null : details.getIdentity();
    }

}
