package org.truenewx.tnxjee.service.exception.model;

import org.truenewx.tnxjee.service.exception.SingleException;

/**
 * 已消息化的错误
 *
 * @author jianglei
 */
public class MessagedError extends CodedError {

    private String type;
    private String field;

    public MessagedError() {
    }

    public MessagedError(SingleException se, String message) {
        super(se.getCode(), message);
        this.type = se.getClass().getName();
        this.field = se.getProperty();
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
