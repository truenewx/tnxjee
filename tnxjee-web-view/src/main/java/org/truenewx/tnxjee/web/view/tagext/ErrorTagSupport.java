package org.truenewx.tnxjee.web.view.tagext;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.controller.exception.message.BusinessExceptionMessageSaver;
import org.truenewx.tnxjee.web.controller.exception.resolver.ResolvedBusinessError;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.List;

/**
 * 错误标签支持
 *
 * @author jianglei
 */
public abstract class ErrorTagSupport extends TagSupport {

    private static final long serialVersionUID = -1567572110106962210L;

    protected String field = Strings.ASTERISK;
    private boolean inverse;

    public void setField(String field) {
        this.field = field;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    @SuppressWarnings("unchecked")
    protected List<ResolvedBusinessError> getErrors() {
        ServletRequest request = this.pageContext.getRequest();
        return (List<ResolvedBusinessError>) request
                .getAttribute(BusinessExceptionMessageSaver.ATTRIBUTE);
    }

    protected final boolean matches() {
        if (this.inverse) {
            return !hasError();
        } else {
            return hasError();
        }
    }

    private boolean hasError() {
        List<ResolvedBusinessError> errors = getErrors();
        if (errors != null) {
            if (Strings.ASTERISK.equals(this.field)) {
                return errors.size() > 0;
            }
            for (ResolvedBusinessError error : errors) {
                if (this.field.equals(error.getField())) {
                    return true;
                }
            }
        }
        return false;
    }

}
