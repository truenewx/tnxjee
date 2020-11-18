package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.webmvc.security.authentication.SmsVerifyCodeAuthenticationToken;

/**
 * 短信登录进程过滤器
 */
public class SmsLoginProcessingFilter extends AbstractLoginProcessingFilter {

    public static final String DEFAULT_PARAMETER_MOBILE_PHONE = "mobilePhone";
    public static final String DEFAULT_PARAMETER_VERIFY_CODE = "verifyCode";

    private String mobilePhoneParameter = DEFAULT_PARAMETER_MOBILE_PHONE;
    private String verifyCodeParameter = DEFAULT_PARAMETER_VERIFY_CODE;

    public SmsLoginProcessingFilter(String defaultFilterProcessesUrl, ApplicationContext context) {
        super(defaultFilterProcessesUrl, context);
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

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
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

        SmsVerifyCodeAuthenticationToken authRequest = new SmsVerifyCodeAuthenticationToken(mobilePhone, verifyCode);
        setDetails(request, authRequest);
        return getAuthenticationManager().authenticate(authRequest);
    }

}
