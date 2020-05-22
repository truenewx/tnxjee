package org.truenewx.tnxjee.web.view.servlet.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.web.view.servlet.http.SessionIdRequestWrapper;
import org.truenewx.tnxjee.web.view.servlet.http.SessionIdResponseWrapper;

/**
 * 准备上下文环境的过滤器
 */
public class PrepareContextFilter implements Filter {
    /**
     * 根路径属性名
     */
    private String contextPathAttributeName = "context";
    /**
     * 是否伪造sessionId，以解决http和https之间切换时sessionId丢失的问题。
     * 当一个站点同时存在http和https链接时需设置为true
     */
    private boolean fakeSessionId;

    public void setContextPathAttributeName(String contextPathAttributeName) {
        this.contextPathAttributeName = contextPathAttributeName;
    }

    public void setFakeSessionId(boolean fakeSessionId) {
        this.fakeSessionId = fakeSessionId;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String contextPathAttributeName = filterConfig.getInitParameter("contextPathAttributeName");
        if (contextPathAttributeName != null) {
            this.contextPathAttributeName = contextPathAttributeName;
        }
        if (Boolean.parseBoolean(filterConfig.getInitParameter("fakeSessionId"))) {
            this.fakeSessionId = true;
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (this.fakeSessionId) {
            request = new SessionIdRequestWrapper(request, response);
            response = new SessionIdResponseWrapper(request, response);
        }
        // 生成简单的相对访问根路径属性
        if (StringUtils.isNotBlank(this.contextPathAttributeName)) {
            request.setAttribute(this.contextPathAttributeName, request.getContextPath());
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
