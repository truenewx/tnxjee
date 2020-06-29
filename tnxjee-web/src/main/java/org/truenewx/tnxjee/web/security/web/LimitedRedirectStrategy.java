package org.truenewx.tnxjee.web.security.web;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * 具有限制的重定向策略
 */
@Component
public class LimitedRedirectStrategy extends DefaultRedirectStrategy {

    private List<String> redirectWhileList;

    public void setRedirectWhileList(List<String> redirectWhileList) {
        this.redirectWhileList = redirectWhileList;
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

        response.sendRedirect(redirectUrl);
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
