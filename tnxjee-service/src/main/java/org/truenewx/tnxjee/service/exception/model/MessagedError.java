package org.truenewx.tnxjee.service.exception.model;

/**
 * 已消息化的错误
 *
 * @author jianglei
 */
public class MessagedError {

    private String message;
    private String code;
    private String field;

    public MessagedError() {
    }

    public MessagedError(String message, String code, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
