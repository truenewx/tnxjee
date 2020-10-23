package org.truenewx.tnxjee.webmvc.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * OAuth2客户端认证Token
 */
public class OAuth2ClientAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 9087828963670023544L;

    private final Object principal;
    private String credentials;

    public OAuth2ClientAuthenticationToken(Object principal, String credentials) {
        super(null);
        setAuthenticated(false);
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted");
        }

        super.setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
