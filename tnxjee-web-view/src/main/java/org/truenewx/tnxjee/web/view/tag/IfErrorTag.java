package org.truenewx.tnxjee.web.view.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.truenewx.tnxjee.web.view.tagext.ErrorTagSupport;

/**
 * 如果存在错误消息则执行标签体的标签
 *
 * @author jianglei
 */
public class IfErrorTag extends ErrorTagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    @Override
    public int doStartTag() throws JspException {
        return matches() ? Tag.EVAL_BODY_INCLUDE : Tag.SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }
}
