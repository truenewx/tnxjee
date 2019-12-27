package org.truenewx.tnxjee.model.user.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.truenewx.tnxjee.model.core.CloneableForSession;
import org.truenewx.tnxjee.model.user.UserIdentity;
import org.truenewx.tnxjee.model.user.UserSpecific;

/**
 * 具有用户特性细节的
 *
 * @param <I> 用户标识类型
 */
public interface UserSpecificDetails<I extends UserIdentity>
        extends UserSpecific<I>, UserDetails, CloneableForSession<UserSpecificDetails<I>> {

    @Override
    default boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    default boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    default boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    @Override
    default boolean isEnabled() {
        return !isDisabled();
    }

    boolean isDisabled();

}
