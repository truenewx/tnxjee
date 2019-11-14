package org.truenewx.tnxjee.web.controller.exception.resolver;

import org.truenewx.tnxjee.service.api.exception.BusinessException;

/**
 * 已处理的错误模型
 *
 * @author jianglei
 */
public class HandledError {

    private String message;
    private String code;
    private String field;

    public static HandledError of(String message, BusinessException be) {
        return new HandledError(message, be.getCode(), be.getProperty());
    }

    public HandledError(String message, String code, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public HandledError(String message, String code) {
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
