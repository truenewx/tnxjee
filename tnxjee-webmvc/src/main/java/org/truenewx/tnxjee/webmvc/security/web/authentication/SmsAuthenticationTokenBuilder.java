package org.truenewx.tnxjee.webmvc.security.web.authentication;

import javax.servlet.http.HttpServletRequest;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.webmvc.security.authentication.SmsVerifyCodeAuthenticationToken;

/**
 * 短信登录认证令牌构建器
 */
public class SmsAuthenticationTokenBuilder
        extends AbstractAuthenticationTokenBuilder<SmsVerifyCodeAuthenticationToken> {

    public static final String DEFAULT_PARAMETER_MOBILE_PHONE = "mobilePhone";
    public static final String DEFAULT_PARAMETER_VERIFY_CODE = "verifyCode";

    private String mobilePhoneParameter = DEFAULT_PARAMETER_MOBILE_PHONE;
    private String verifyCodeParameter = DEFAULT_PARAMETER_VERIFY_CODE;

    public SmsAuthenticationTokenBuilder(String loginMode) {
        super(loginMode);
    }

    @Override
    public SmsVerifyCodeAuthenticationToken buildAuthenticationToken(HttpServletRequest request) {
        String mobilePhone = request.getParameter(getMobilePhoneParameter());
        String verifyCode = request.getParameter(getVerifyCodeParameter());
        if (mobilePhone == null) {
            mobilePhone = Strings.EMPTY;
        }
        mobilePhone = mobilePhone.trim();
        if (verifyCode == null) {
            verifyCode = Strings.EMPTY;
        }
        verifyCode = verifyCode.trim();

        return new SmsVerifyCodeAuthenticationToken(mobilePhone, verifyCode);
    }

    public String getMobilePhoneParameter() {
        return this.mobilePhoneParameter;
    }

    public void setMobilePhoneParameter(String mobilePhoneParameter) {
        this.mobilePhoneParameter = mobilePhoneParameter;
    }

    public String getVerifyCodeParameter() {
        return this.verifyCodeParameter;
    }

    public void setVerifyCodeParameter(String verifyCodeParameter) {
        this.verifyCodeParameter = verifyCodeParameter;
    }

}
