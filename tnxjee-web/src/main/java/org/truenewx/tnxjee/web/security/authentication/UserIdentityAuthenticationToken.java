package org.truenewx.tnxjee.web.security.authentication;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 用户标识的认证令牌
 */
public class UserIdentityAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = -5060938946411675544L;

    private UserIdentity<?> userIdentity;

    public UserIdentityAuthenticationToken(UserIdentity<?> userIdentity) {
        super(Collections.emptyList());
        super.setAuthenticated(true);
        this.userIdentity = userIdentity;
    }

    @Override
    public Object getPrincipal() {
        return this.userIdentity;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new UnsupportedOperationException();
    }

}
