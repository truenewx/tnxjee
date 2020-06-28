package org.truenewx.tnxjee.web.security.web;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * AJAX请求特殊处理的重定向策略
 */
@Component
public class AjaxRedirectStrategy extends DefaultRedirectStrategy {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    private List<String> redirectWhileList;

    public void setRedirectWhileList(List<String> redirectWhileList) {
        this.redirectWhileList = redirectWhileList;
    }

    public boolean isAjaxRequest(HttpServletRequest request) {
        return this.handlerMethodMapping.isAjaxRequest(request);
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response,
            String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        if (!isValidRedirectUrl(request, response, redirectUrl)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "illegal redirect target url.");
            return;
        }
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Redirecting to '" + redirectUrl + "'");
        }

        if (isAjaxRequest(request)) {
            // ajax重定向时，js端自动跳转不会带上origin头信息，导致目标站点cors校验失败。
            // 不得已只能将目标地址放到头信息中传递给js端，由js执行跳转以带上origin头信息，使得目标站点cors校验通过，
            // 同时返回401状态码标识错误。
            response.setHeader(WebConstants.HEADER_REDIRECT_TO, redirectUrl);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            response.sendRedirect(redirectUrl);
        }
    }

    protected boolean isValidRedirectUrl(HttpServletRequest request, HttpServletResponse response,
            String redirectUrl) throws IOException {
        // 相对路径地址可以重定向
        if (NetUtil.isRelativeUrl(redirectUrl)) {
            return true;
        }
        String requestHost = WebUtil.getHost(request, false);
        String redirectHost = NetUtil.getHost(redirectUrl, false);
        // 同一个主机地址可以重定向，即使端口可能不同
        if (Objects.equals(requestHost, redirectHost)) {
            return true;
        }
        // 同为本机地址可以重定向
        if (NetUtil.isLocalHost(requestHost) && NetUtil.isLocalHost(redirectHost)) {
            return true;
        }
        // 同为内网IP可以重定向，即使网段可能不同
        if (NetUtil.isIntranetIp(redirectHost) && NetUtil.isIntranetIp(redirectUrl)) {
            return true;
        }
        // 同一个顶级域名可以重定向
        String requestDomain = NetUtil.getTopDomain(requestHost);
        String redirectDomain = NetUtil.getTopDomain(redirectHost);
        if (Objects.equals(requestDomain, redirectDomain)) {
            return true;
        }
        // 匹配白名单可以重定向
        return StringUtil.antPathMatchOneOf(redirectUrl, this.redirectWhileList);
    }

}
