package org.truenewx.tnxjee.web.view.tag;

import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.servlet.mvc.Loginer;
import org.truenewx.tnxjee.web.util.SpringWebUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * 输出前一个请求URL的标签
 *
 * @author jianglei
 */
public class PrevUrlTag extends TagSupport {

    private static final long serialVersionUID = 8123676932054182255L;

    private String defaultHref = "javascript:history.back(-1)";
    private boolean withContext = true;

    public void setDefault(String defaultHref) {
        this.defaultHref = defaultHref;
    }

    public void setContext(boolean context) {
        this.withContext = context;
    }

    @Override
    public int doEndTag() throws JspException {
        // 使用pageContext中的request会得到jsp页面的访问路径，这可能导致错误
        HttpServletRequest request = SpringWebContext.getRequest();
        String currentAction = WebUtil.getRelativeRequestAction(request);
        String prevUrl = WebViewUtil.getRelativePreviousUrl(request, true);
        if (prevUrl != null) {
            if (prevUrl.startsWith(currentAction)) { // 如果前一页url以当前action开头，则执行默认的前一页规则，以避免跳转相同页
                prevUrl = null;
            } else {
                ApplicationContext context = SpringWebUtil.getApplicationContext(request);
                Loginer loginer = SpringUtil.getFirstBeanByClass(context, Loginer.class);
                if (loginer != null && prevUrl != null && loginer.isLoginUrl(prevUrl)) {
                    prevUrl = null;
                }
            }
        }
        JspWriter out = this.pageContext.getOut();
        try {
            if (prevUrl != null) {
                if (this.withContext) {
                    String contextPath = request.getContextPath();
                    if (!contextPath.equals(Strings.SLASH)) {
                        out.print(contextPath);
                    }
                }
                out.print(prevUrl);
            } else {
                out.print(this.defaultHref);
            }
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }
}
