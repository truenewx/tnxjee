package org.truenewx.tnxjee.web.security.web;

import java.io.IOException;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.web.util.WebConstants;

/**
 * AJAX请求特殊处理的重定向策略
 */
@Component
public class AjaxRedirectStrategy extends DefaultRedirectStrategy {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    private Predicate<String> redirectUrlPredicate = url -> true;

    public void setRedirectUrlPredicate(Predicate<String> redirectUrlPredicate) {
        this.redirectUrlPredicate = redirectUrlPredicate;
    }

    public boolean isAjaxRequest(HttpServletRequest request) {
        return this.handlerMethodMapping.isAjaxRequest(request);
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response,
            String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        if (!this.redirectUrlPredicate.test(redirectUrl)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "illegal redirect target url.");
            return;
        }
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Redirecting to '" + redirectUrl + "'");
        }

        if (isAjaxRequest(request)) {
            // ajax重定向时，js端自动跳转不会带上origin头信息，导致目标站点cors校验失败。
            // 不得已只能将目标地址放到头信息中传递给js端，由js执行跳转以带上origin头信息，使得目标站点cors校验通过。
            response.setHeader(WebConstants.HEADER_REDIRECT, redirectUrl);
        } else {
            response.sendRedirect(redirectUrl);
        }
    }

}
