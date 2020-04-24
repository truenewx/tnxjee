package org.truenewx.tnxjee.web.view.tagext;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.exception.message.ResolvableExceptionMessageSaver;
import org.truenewx.tnxjee.web.exception.resolver.ResolvedBusinessError;

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
    protected List<ResolvedBusinessError> getErrors() {
        ServletRequest request = this.pageContext.getRequest();
        return (List<ResolvedBusinessError>) request
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
