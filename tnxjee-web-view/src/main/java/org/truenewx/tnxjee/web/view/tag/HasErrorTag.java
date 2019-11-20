package org.truenewx.tnxjee.web.view.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.web.view.tagext.ErrorTagSupport;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;

/**
 * 是否存在错误消息的标签
 *
 * @author jianglei
 */
public class HasErrorTag extends ErrorTagSupport {


//    public int doStartTag() throws JspException {
//        return matches() ? Tag.EVAL_BODY_INCLUDE : Tag.SKIP_BODY;
//    }
//
//    public int doEndTag() throws JspException {
//        return Tag.EVAL_PAGE;
//    }

    @Override
    protected String getTagName() {
        return "has-error";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {

    }
}
