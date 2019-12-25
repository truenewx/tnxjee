package org.truenewx.tnxjee.model.user.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * 用户已取得的权限
 */
public class UserGrantedAuthority extends UserAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 7591271389253138160L;

    public UserGrantedAuthority(String role, String permission) {
        super(role, permission);
    }

    public UserGrantedAuthority(String s) {
        super(s);
    }

    @Override
    public String getAuthority() {
        return toString();
    }
}
