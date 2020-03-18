package org.truenewx.tnxjee.web.controller.security.access;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 获权判定器
 */
public interface GrantedAuthorityDecider {

    /**
     * 判断指定角色和许可在指定的获权集合范围内是否为已获权
     *
     * @param authorities 获权集合
     * @param role        角色
     * @param permission  许可
     * @return 是否为已获权
     */
    boolean isGranted(Collection<? extends GrantedAuthority> authorities, String role, String permission);

}
