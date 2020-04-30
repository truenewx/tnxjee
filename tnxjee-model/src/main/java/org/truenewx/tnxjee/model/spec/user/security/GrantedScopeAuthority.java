package org.truenewx.tnxjee.model.spec.user.security;

import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.core.Strings;

/**
 * 已取得的范围权限
 */
public class GrantedScopeAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 2247499957479720543L;

    private String type;
    private String rank;

    public GrantedScopeAuthority(String type, String rank) {
        this.type = type;
        this.rank = rank;
    }

    public String getType() {
        return this.type;
    }

    public String getRank() {
        return this.rank;
    }

    @Override
    public String getAuthority() {
        return "SCOPE_" + this.type + Strings.DOT + this.rank;
    }

    @Override
    public String toString() {
        return getAuthority();
    }
}
