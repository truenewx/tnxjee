package org.truenewx.tnxjee.web.exception.resolver;

import org.truenewx.tnxjee.service.exception.BusinessException;

/**
 * 已解决的业务错误
 *
 * @author jianglei
 */
public class ResolvedBusinessError {

    private String message;
    private String code;
    private String field;

    public static ResolvedBusinessError of(String message, BusinessException be) {
        return new ResolvedBusinessError(message, be.getCode(), be.getProperty());
    }

    public ResolvedBusinessError(String message, String code, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public ResolvedBusinessError(String message, String code) {
        this(message, code, null);
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }

    public String getField() {
        return this.field;
    }

}
