package org.truenewx.tnxjee.web.view.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.truenewx.tnxjee.web.view.tagext.ErrorTagSupport;

/**
 * 判断是否没有错误消息的标签
 *
 * @author jianglei
 */
public class NoErrorTag extends ErrorTagSupport {

    private static final long serialVersionUID = 7787334341554597267L;

    @Override
    public int doStartTag() throws JspException {
        return matches() ? Tag.SKIP_BODY : Tag.EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }
}
