package org.truenewx.tnxjee.web.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.truenewx.tnxjee.core.Strings;

/**
 * 掩盖Email的标签
 *
 * @author jianglei
 */
public class CoverEmailTag extends SimpleTagSupport {

    private String value;
    private char coverChar = '*';

    public void setValue(String value) {
        this.value = value;
    }

    public void setCoverChar(char coverChar) {
        this.coverChar = coverChar;
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (this.value != null) {
            int index = this.value.indexOf(Strings.AT);
            if (index > 0) {
                String name = this.value.substring(0, index);
                int length = name.length();
                int coverBeginIndex = length / 4;
                JspWriter out = getJspContext().getOut();
                out.print(name.substring(0, coverBeginIndex));
                int coverEndIndex = coverBeginIndex + length / 2;
                for (int i = coverBeginIndex; i < coverEndIndex; i++) {
                    out.print(this.coverChar);
                }
                out.print(name.substring(coverEndIndex));
                out.print(this.value.substring(index));
            }
        }
    }

}
