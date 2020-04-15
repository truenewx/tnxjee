package org.truenewx.tnxjee.web.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 用户特性细节的认证令牌
 */
public class UserSpecificDetailsAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 5719790668377346866L;

    public UserSpecificDetailsAuthenticationToken(UserSpecificDetails<?> details) {
        super(details.getAuthorities());
        setDetails(details.cloneForSession());
    }

    @Override
    public Object getPrincipal() {
        return ((UserSpecificDetails<?>) getDetails()).getUsername();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new UnsupportedOperationException();
    }
}
