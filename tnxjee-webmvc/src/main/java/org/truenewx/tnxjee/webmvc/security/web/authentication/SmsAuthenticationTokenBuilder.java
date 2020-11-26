package org.truenewx.tnxjee.webmvc.security.web.authentication;

import javax.servlet.http.HttpServletRequest;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.webmvc.security.authentication.SmsVerifyCodeAuthenticationToken;

/**
 * 短信登录认证令牌构建器
 */
public class SmsAuthenticationTokenBuilder
        extends AbstractAuthenticationTokenBuilder<SmsVerifyCodeAuthenticationToken> {

    public static final String DEFAULT_PARAMETER_CELLPHONE = "cellphone";
    public static final String DEFAULT_PARAMETER_VERIFY_CODE = "verifyCode";

    private String cellphoneParameter = DEFAULT_PARAMETER_CELLPHONE;
    private String verifyCodeParameter = DEFAULT_PARAMETER_VERIFY_CODE;

    public SmsAuthenticationTokenBuilder(String loginMode) {
        super(loginMode);
    }

    @Override
    public SmsVerifyCodeAuthenticationToken buildAuthenticationToken(HttpServletRequest request) {
        String cellphone = request.getParameter(getCellphoneParameter());
        String verifyCode = request.getParameter(getVerifyCodeParameter());
        if (cellphone == null) {
            cellphone = Strings.EMPTY;
        }
        cellphone = cellphone.trim();
        if (verifyCode == null) {
            verifyCode = Strings.EMPTY;
        }
        verifyCode = verifyCode.trim();

        return new SmsVerifyCodeAuthenticationToken(cellphone, verifyCode);
    }

    public String getCellphoneParameter() {
        return this.cellphoneParameter;
    }

    public void setCellphoneParameter(String cellphoneParameter) {
        this.cellphoneParameter = cellphoneParameter;
    }

    public String getVerifyCodeParameter() {
        return this.verifyCodeParameter;
    }

    public void setVerifyCodeParameter(String verifyCodeParameter) {
        this.verifyCodeParameter = verifyCodeParameter;
    }

}
