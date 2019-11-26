package org.truenewx.tnxjee.web.view.tagext;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.tnxjee.web.controller.exception.resolver.ResolvedBusinessError;
import org.truenewx.tnxjee.web.view.exception.resolver.ViewBusinessExceptionResolver;

/**
 * 错误标签支持
 *
 * @author jianglei
 */
public abstract class ErrorTagSupport extends TagSupport {

    private static final long serialVersionUID = -1567572110106962210L;

    protected String field;

    public void setField(String field) {
        this.field = field;
    }

    @SuppressWarnings("unchecked")
    protected List<ResolvedBusinessError> getErrors() {
        ServletRequest request = this.pageContext.getRequest();
        return (List<ResolvedBusinessError>) request
                .getAttribute(ViewBusinessExceptionResolver.ATTRIBUTE_ERRORS);
    }

}
