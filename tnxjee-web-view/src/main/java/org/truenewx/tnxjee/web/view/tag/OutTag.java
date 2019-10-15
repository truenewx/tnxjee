package org.truenewx.tnxjee.web.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * 依次从request.parameters, request.attributes, session, application中取出指定变量值输出的标签
 *
 * @author jianglei
 */
public class OutTag extends TagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    private String name;
    private String value;
    private boolean remove;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) throws JspException {
        this.value = (String) ExpressionEvaluatorManager.evaluate("value", value, Object.class,
                this.pageContext);
    }

    public void setRemove(String remove) {
        this.remove = Boolean.valueOf(remove);
    }

    @Override
    public int doEndTag() throws JspException {
        // 依次从request.parameters, request.attributes, session, application中取值
        Object o = this.pageContext.getRequest().getParameter(this.name);
        if (o == null) {
            o = this.pageContext.getRequest().getAttribute(this.name);
        }
        boolean removeFromSession = false;
        if (o == null) {
            o = this.pageContext.getSession().getAttribute(this.name);
            removeFromSession = this.remove;
        }
        if (o == null) {
            o = this.pageContext.getServletContext().getAttribute(this.name);
        }
        final JspWriter out = this.pageContext.getOut();
        try {
            if (o != null) {
                out.print(o.toString());
            } else if (this.value != null) {
                out.print(this.value);
            }
        } catch (final IOException e) {
            throw new JspException(e);
        }
        if (removeFromSession) {
            this.pageContext.getSession().removeAttribute(this.name);
        }
        return EVAL_PAGE;
    }

}
