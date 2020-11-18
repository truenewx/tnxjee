package org.truenewx.tnxjee.webmvc.security.authentication;

/**
 * OAuth2客户端认证令牌
 */
public class OAuth2ClientAuthenticationToken extends UnauthenticatedAuthenticationToken {

    private static final long serialVersionUID = 9087828963670023544L;

    public OAuth2ClientAuthenticationToken(Object user, String loginCode) {
        super(user, loginCode);
    }

    public Object getUser() {
        return getPrincipal();
    }

    public String getLoginCode() {
        return (String) getCredentials();
    }

}
