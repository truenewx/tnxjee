package org.truenewx.tnxjee.model.spec.user.security;

import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.core.Strings;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 已取得权限的实现
 */
public class GrantedAuthorityImpl implements GrantedAuthority {

    private static final long serialVersionUID = 7686101572750400289L;

    private GrantedAuthorityKind kind;
    private String name;

    public GrantedAuthorityImpl() {
    }

    public GrantedAuthorityImpl(GrantedAuthorityKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    public GrantedAuthorityKind getKind() {
        return this.kind;
    }

    public void setKind(GrantedAuthorityKind kind) {
        this.kind = kind;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        return this.name + Strings.AT + this.kind;
    }

}
