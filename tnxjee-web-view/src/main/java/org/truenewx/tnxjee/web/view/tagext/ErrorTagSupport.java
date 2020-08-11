package org.truenewx.tnxjee.web.view.tagext;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.exception.model.MessagedError;
import org.truenewx.tnxjee.web.exception.message.ResolvableExceptionMessageSaver;

/**
 * 错误标签支持
 *
 * @author jianglei
 */
public abstract class ErrorTagSupport extends TagSupport {

    private static final long serialVersionUID = -1567572110106962210L;

    protected String field = Strings.ASTERISK;

    public void setField(String field) {
        this.field = field;
    }

    @SuppressWarnings("unchecked")
    protected List<MessagedError> getErrors() {
        ServletRequest request = this.pageContext.getRequest();
        return (List<MessagedError>) request
                .getAttribute(ResolvableExceptionMessageSaver.ATTRIBUTE);
    }

    protected final boolean matches(boolean inverse) {
        if (inverse) {
            return !hasError();
        } else {
            return hasError();
        }
    }

    private boolean hasError() {
        List<MessagedError> errors = getErrors();
        if (errors != null) {
            if (Strings.ASTERISK.equals(this.field)) {
                return errors.size() > 0;
            }
            for (MessagedError error : errors) {
                if (this.field.equals(error.getField())) {
                    return true;
                }
            }
        }
        return false;
    }

}
