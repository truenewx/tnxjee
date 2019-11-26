package org.truenewx.tnxjee.web.view.tag;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.controller.exception.resolver.ResolvedBusinessError;
import org.truenewx.tnxjee.web.view.tagext.ErrorTagSupport;

/**
 * 判断是否存在错误消息的标签
 *
 * @author jianglei
 */
public class HasErrorTag extends ErrorTagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    @Override
    public int doStartTag() throws JspException {
        return hasError() ? Tag.EVAL_BODY_INCLUDE : Tag.SKIP_BODY;
    }

    protected final boolean hasError() {
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

    @Override
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }
}
