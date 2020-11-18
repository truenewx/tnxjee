package org.truenewx.tnxjee.webmvc.security.authentication;

/**
 * 短信验证码认证令牌
 */
public class SmsVerifyCodeAuthenticationToken extends UnauthenticatedAuthenticationToken {
    
    private static final long serialVersionUID = -8429113208857575276L;

    public SmsVerifyCodeAuthenticationToken(String mobilePhone, String verifyCode) {
        super(mobilePhone, verifyCode);
    }

    public String getMobilePhone() {
        return (String) getPrincipal();
    }

    public String getVerifyCode() {
        return (String) getCredentials();
    }

}
