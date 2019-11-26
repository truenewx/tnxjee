package org.truenewx.tnxjee.web.view.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

/**
 * 判断是否没有错误消息的标签
 *
 * @author jianglei
 */
public class NoErrorTag extends HasErrorTag {

    private static final long serialVersionUID = 7787334341554597267L;

    @Override
    public int doStartTag() throws JspException {
        return hasError() ? Tag.SKIP_BODY : Tag.EVAL_BODY_INCLUDE;
    }

}
