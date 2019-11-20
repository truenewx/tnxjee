package org.truenewx.tnxjee.web.view.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.web.view.tagext.ErrorTagSupport;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;

/**
 * 判断是否没有错误消息的标签
 *
 * @author jianglei
 */
public class NoErrorTag extends ErrorTagSupport {


//    @Override
//    public int doStartTag() throws JspException {
//        return matches() ? Tag.SKIP_BODY : Tag.EVAL_BODY_INCLUDE;
//    }
//
//    @Override
//    public int doEndTag() throws JspException {
//        return Tag.EVAL_PAGE;
//    }

    @Override
    protected String getTagName() {
        return "no-error";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {

    }
}
