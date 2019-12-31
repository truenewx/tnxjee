package org.truenewx.tnxjee.web.view.tag;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.controller.util.SpringWebUtil;

/**
 * 国际化消息标签
 *
 * @author jianglei
 */
public class MessageTag extends SimpleTagSupport {

    private String code;
    private String[] args;
    private ApplicationContext context;

    public void setCode(String code) {
        this.code = code;
    }

    public void setArgs(String args) {
        this.args = args.trim().split(Strings.COMMA);
    }

    private ApplicationContext getApplicationContext() {
        if (this.context == null) {
            HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext())
                    .getRequest();
            this.context = SpringWebUtil.getApplicationContext(request);
        }
        return this.context;
    }

    private Locale getLocale() {
        HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext())
                .getRequest();
        return SpringWebUtil.getLocale(request);
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (StringUtils.isNotBlank(this.code)) {
            String message = getApplicationContext().getMessage(this.code, this.args, null,
                    getLocale());
            if (message != null) {
                getJspContext().getOut().print(message);
            }
        }
    }

}
