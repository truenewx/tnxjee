package org.truenewx.tnxjee.web.http.session;

import java.io.IOException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.truenewx.tnxjee.core.util.BeanUtil;

public class HeaderSessionIdFilter implements Filter {

    public static final int DEFAULT_ORDER = SessionRepositoryFilter.DEFAULT_ORDER + 1;

    private HeaderSessionIdReader reader;
    private HttpSessionIdResolver resolver;

    public HeaderSessionIdFilter(HeaderSessionIdReader reader,
            SessionRepositoryFilter<?> sessionRepositoryFilter) {
        this.reader = reader;
        this.resolver = BeanUtil.getFieldValue(sessionRepositoryFilter, HttpSessionIdResolver.class);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && this.reader != null && this.resolver != null) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            List<String> sessionIds = this.reader.readSessionIds(httpRequest);
            // 将header中的sessionId写入响应Cookie中，以便于下次请求时无需header中带sessionId也能访问
            sessionIds.forEach(sessionId -> {
                this.resolver.setSessionId(httpRequest, (HttpServletResponse) response, sessionId);
            });
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
