package org.truenewx.tnxjee.web.exception.model;

import java.util.Collection;
import org.truenewx.tnxjee.service.exception.model.MessagedError;

/**
 * 错误消息体
 */
public class MessagedErrorBody {

    private MessagedError[] errors;

    public MessagedErrorBody() {
    }

    public MessagedErrorBody(Collection<MessagedError> errors) {
        this.errors = errors.toArray(new MessagedError[errors.size()]);
    }

    public MessagedError[] getErrors() {
        return this.errors;
    }

    public void setErrors(MessagedError[] errors) {
        this.errors = errors;
    }

}
