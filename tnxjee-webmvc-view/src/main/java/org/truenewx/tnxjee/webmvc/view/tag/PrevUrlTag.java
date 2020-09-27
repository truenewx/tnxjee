package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.webmvc.context.SpringWebmvcContext;
import org.truenewx.tnxjee.webmvc.servlet.mvc.LoginUrlResolver;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;
import org.truenewx.tnxjee.webmvc.util.WebMvcUtil;
import org.truenewx.tnxjee.webmvc.view.util.WebViewUtil;

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
        HttpServletRequest request = SpringWebmvcContext.getRequest();
        String currentAction = WebMvcUtil.getRelativeRequestAction(request);
        String prevUrl = WebViewUtil.getRelativePreviousUrl(request, true);
        if (prevUrl != null) {
            if (prevUrl.startsWith(currentAction)) { // 如果前一页url以当前action开头，则执行默认的前一页规则，以避免跳转相同页
                prevUrl = null;
            } else {
                ApplicationContext context = SpringWebMvcUtil.getApplicationContext(request);
                LoginUrlResolver loginUrlResolver = SpringUtil.getFirstBeanByClass(context, LoginUrlResolver.class);
                if (loginUrlResolver != null && loginUrlResolver.isLoginUrl(prevUrl)) {
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
