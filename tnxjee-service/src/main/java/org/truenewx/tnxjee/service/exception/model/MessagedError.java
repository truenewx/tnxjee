package org.truenewx.tnxjee.service.exception.model;

import org.truenewx.tnxjee.service.exception.SingleException;

/**
 * 已消息化的错误
 *
 * @author jianglei
 */
public class MessagedError {

    private String message;
    private String type;
    private String code;
    private String field;

    public MessagedError() {
    }

    public MessagedError(String message, SingleException se) {
        this.message = message;
        this.type = se.getClass().getName();
        this.code = se.getCode();
        this.field = se.getProperty();
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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
