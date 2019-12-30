package org.truenewx.tnxjee.model.spec.user.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * 已取得的角色权限
 */
public class GrantedRoleAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 2247499957479720543L;

    private String role;

    public GrantedRoleAuthority(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.role;
    }

    @Override
    public String toString() {
        return getAuthority();
    }
}
