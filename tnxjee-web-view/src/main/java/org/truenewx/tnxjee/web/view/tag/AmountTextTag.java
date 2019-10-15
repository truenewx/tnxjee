package org.truenewx.tnxjee.web.view.tag;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;

/**
 * 金额显示标签
 *
 * @author jianglei
 */
public class AmountTextTag extends TagSupport {

    private static final long serialVersionUID = -4683965211565778127L;

    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int doEndTag() throws JspException {
        if (this.value == null) {
            this.value = Strings.EMPTY;
        }
        DecimalFormat df = new DecimalFormat("###,###.##");
        JspWriter out = this.pageContext.getOut();
        try {
            out.print(df.format(MathUtil.parseDecimal(this.value)));
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }
}
