package org.truenewx.tnxjee.web.security.access;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * 获权判定器
 */
public interface GrantedAuthorityDecider {

    /**
     * 判断指定用户类型、用户级别和许可在指定的获权集合范围内是否为已获权
     *
     * @param authorities 获权集合
     * @param type        用户类型
     * @param rank        用户级别
     * @param permission  许可
     * @return 是否为已获权
     */
    boolean isGranted(Collection<? extends GrantedAuthority> authorities, String type,
            String rank, String permission);

}
