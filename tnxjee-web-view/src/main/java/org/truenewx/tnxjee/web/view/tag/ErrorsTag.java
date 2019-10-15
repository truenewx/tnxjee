package org.truenewx.tnxjee.web.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;

import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.spring.util.SpringUtil;
import org.truenewx.tnxjee.model.exception.MultiException;
import org.truenewx.tnxjee.model.exception.SingleException;
import org.truenewx.tnxjee.web.controller.exception.message.SingleExceptionMessageResolver;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;
import org.truenewx.tnxjee.web.view.tagext.ErrorTagSupport;

/**
 * 输出错误消息的标签
 *
 * @author jianglei
 */
public class ErrorsTag extends ErrorTagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    private String delimiter = "<br/>";
    private String suffix;

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public int doEndTag() throws JspException {
        Object obj = getException();
        if (obj != null) {
            ApplicationContext context = SpringWebUtil.getApplicationContext(this.pageContext);
            SingleExceptionMessageResolver messageResolver = SpringUtil
                    .getBeanByDefaultName(context, SingleExceptionMessageResolver.class);

            StringBuffer message = new StringBuffer();
            if (obj instanceof SingleException) {
                SingleException se = (SingleException) obj;
                if (se.matches(this.field)) {
                    message.append(messageResolver.resolveMessage(se,
                            this.pageContext.getRequest().getLocale()));
                }
            } else if (obj instanceof MultiException) {
                MultiException me = (MultiException) obj;
                // 遍历同一filed中的多个异常信息
                for (SingleException se : me) {
                    if (se.matches(this.field)) {
                        message.append(messageResolver.resolveMessage(se,
                                this.pageContext.getRequest().getLocale()));
                        message.append(this.delimiter);
                    }
                }
                if (message.length() > 0) {
                    message.delete(message.length() - this.delimiter.length(), message.length());
                }
            }
            if (message.length() > 0 && this.suffix != null) { // 有内容时才加后缀
                message.append(this.suffix);
            }

            JspWriter out = this.pageContext.getOut();
            try {
                out.print(message);
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
        return Tag.EVAL_PAGE;
    }
}
