package org.truenewx.tnxjee.model.spec.user.security;

import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.core.Strings;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 具有性质的已获取权限
 */
public interface KindGrantedAuthority extends GrantedAuthority {

    GrantedAuthorityKind getKind();

    String getName();

    @Override
    @JsonIgnore
    default String getAuthority() {
        return getName() + Strings.AT + getKind().name();
    }

}
