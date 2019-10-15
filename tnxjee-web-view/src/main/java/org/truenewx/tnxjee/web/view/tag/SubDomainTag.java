package org.truenewx.tnxjee.web.view.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.util.PlaceholderResolver;
import org.truenewx.tnxjee.core.spring.util.SpringUtil;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;

/**
 * 子域名标签
 *
 * @author jianglei
 */
public class SubDomainTag extends TagSupport {

    private static final long serialVersionUID = 2124462419592642526L;

    private String id;
    private boolean print = true;
    private String topDomain;

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }

    public void setTopDomainPlaceholder(String topDomainPlaceholder) {
        ApplicationContext context = SpringWebUtil.getApplicationContext(this.pageContext);
        if (context != null) {
            this.topDomain = SpringUtil.getBeanByDefaultName(context, PlaceholderResolver.class)
                    .resolvePlaceholder(topDomainPlaceholder);
        }
    }

    @Override
    public int doEndTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        if (this.topDomain != null && !this.topDomain.startsWith("localhost")
                && !this.topDomain.startsWith("127.0.0.1")) {
            String host = WebViewUtil.getHost(request);
            String subDomain = "";
            if (host.length() > this.topDomain.length()) {
                subDomain = host.substring(0, host.length() - this.topDomain.length());
            }
            if (subDomain.endsWith(Strings.DOT)) {
                subDomain = subDomain.substring(0, subDomain.length() - Strings.DOT.length());
            }
            if (StringUtils.isNotEmpty(this.id)) {
                request.setAttribute(this.id, subDomain);
            }
            if (this.print) {
                try {
                    this.pageContext.getOut().print(subDomain);
                } catch (IOException e) {
                    throw new JspException(e);
                }
            }
        }
        return EVAL_PAGE;
    }
}
