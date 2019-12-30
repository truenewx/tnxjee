package org.truenewx.tnxjee.web.controller.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 用户特性细节的认证令牌
 */
public class UserSpecificDetailsAuthenticationToken extends AbstractAuthenticationToken { // 继承以获得RememberMe能力

    private static final long serialVersionUID = 5719790668377346866L;

    private UserSpecificDetails<?> user;

    public UserSpecificDetailsAuthenticationToken(UserSpecificDetails<?> user) {
        super(user.getAuthorities());
        this.user = user.cloneForSession();
    }

    @Override
    public Object getPrincipal() {
        return this.user;
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
