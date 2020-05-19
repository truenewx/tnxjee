package org.truenewx.tnxjee.model.spec.user.security;

import org.truenewx.tnxjee.core.caption.Caption;

/**
 * 已取得权限的性质
 */
public enum GrantedAuthorityKind {

    @Caption("用户类型")
    USER_TYPE,

    @Caption("用户级别")
    USER_RANK,

    @Caption("许可")
    PERMISSION;

}
