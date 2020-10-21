package org.truenewx.tnxjee.webmvc.security.web;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * AJAX特殊处理的重定向策略
 */
@Component
public class AjaxRedirectStrategy extends DefaultRedirectStrategy {

    private List<String> redirectWhileList;

    public void setRedirectWhileList(List<String> redirectWhileList) {
        this.redirectWhileList = redirectWhileList;
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response,
            String url) throws IOException {
        String contextPath = request.getContextPath();
        if (Strings.SLASH.equals(contextPath)) {
            contextPath = Strings.EMPTY;
        }
        String redirectUrl = calculateRedirectUrl(contextPath, url);
        if (!isValidRedirectUrl(request, response, redirectUrl)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "illegal redirect target url.");
            return;
        }
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Redirecting to '" + redirectUrl + "'");
        }

        if (WebUtil.isAjaxRequest(request)) {
            // 去掉contextPath前缀
            if (contextPath.length() > 0 && redirectUrl.startsWith(contextPath)) {
                redirectUrl = redirectUrl.substring(contextPath.length());
            }
            // ajax重定向时，js端自动跳转不会带上origin头信息，导致目标站点cors校验失败。
            // 不得已只能将目标地址放到头信息中传递给js端，由js执行跳转以带上origin头信息，使得目标站点cors校验通过。
            // 成功和失败的请求都可能产生重定向动作，所以此处不设置响应状态码
            response.setHeader(WebConstants.HEADER_REDIRECT_TO, redirectUrl);
            String body = buildRedirectBody(redirectUrl);
            if (body != null) {
                response.getWriter().print(body);
            }
        } else {
            response.sendRedirect(redirectUrl);
        }
    }

    protected boolean isValidRedirectUrl(HttpServletRequest request, HttpServletResponse response,
            String redirectUrl) throws IOException {
        // 空地址无效
        if (StringUtils.isBlank(redirectUrl)) {
            return false;
        }
        // 相对路径地址可以重定向
        if (NetUtil.isRelativeUrl(redirectUrl)) {
            return true;
        }
        String redirectHost = NetUtil.getHost(redirectUrl, false);
        // 内网地址可以重定向，即使网段可能不同
        if (NetUtil.isIntranetIp(redirectHost)) {
            return true;
        }
        
        String requestHost = WebUtil.getHost(request, false);
        // 同一个主机地址可以重定向，即使端口可能不同
        if (Objects.equals(requestHost, redirectHost)) {
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

    protected String buildRedirectBody(String redirectUrl) {
        return "It should be redirected to " + redirectUrl;
    }

}
