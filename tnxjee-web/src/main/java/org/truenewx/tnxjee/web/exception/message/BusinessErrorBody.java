package org.truenewx.tnxjee.web.exception.message;

import java.util.Collection;

import org.truenewx.tnxjee.service.exception.message.BusinessError;

/**
 * 业务错误消息体
 */
public class BusinessErrorBody {

    private BusinessError[] errors;

    public BusinessErrorBody() {
    }

    public BusinessErrorBody(Collection<BusinessError> errors) {
        this.errors = errors.toArray(new BusinessError[errors.size()]);
    }

    public BusinessError[] getErrors() {
        return this.errors;
    }

    public void setErrors(BusinessError[] errors) {
        this.errors = errors;
    }

}
