package org.truenewx.tnxjee.service.exception.message;

/**
 * 业务错误消息
 *
 * @author jianglei
 */
public class BusinessError {

    private String message;
    private String code;
    private String field;

    public BusinessError() {
    }

    public BusinessError(String message, String code, String field) {
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
