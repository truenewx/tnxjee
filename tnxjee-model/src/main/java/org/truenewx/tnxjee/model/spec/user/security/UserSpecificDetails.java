package org.truenewx.tnxjee.model.spec.user.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.model.spec.user.UserSpecific;

/**
 * 用户特性细节
 *
 * @param <I> 用户标识类型
 */
public interface UserSpecificDetails<I extends UserIdentity<?>>
        extends UserSpecific<I>, UserDetails {
}
