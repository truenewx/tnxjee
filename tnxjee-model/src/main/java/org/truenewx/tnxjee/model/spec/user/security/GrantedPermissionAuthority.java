package org.truenewx.tnxjee.model.spec.user.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * 已取得的许可权限
 *
 * @author jianglei
 */
public class GrantedPermissionAuthority implements GrantedAuthority {

    private static final long serialVersionUID = -1116958447903491389L;

    private String permission;

    public GrantedPermissionAuthority(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return this.permission;
    }

    @Override
    public String getAuthority() {
        return "PERMISSION_" + this.permission;
    }

    @Override
    public String toString() {
        return getAuthority();
    }
}
