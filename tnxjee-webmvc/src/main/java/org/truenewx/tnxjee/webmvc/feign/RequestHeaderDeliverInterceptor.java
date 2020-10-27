package org.truenewx.tnxjee.webmvc.feign;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.jwt.InternalJwtStrategy;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 请求头信息传递拦截器
 */
@Component
public class RequestHeaderDeliverInterceptor implements RequestInterceptor {

    @Autowired(required = false)
    private InternalJwtStrategy internalJwtStrategy;

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            boolean noJwt = true;
            Map<String, Collection<String>> feignHeaders = template.headers();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Feign头信息中未包含的才传递，以避免Feign创建的头信息被改动
                if (!feignHeaders.containsKey(headerName)) {
                    Enumeration<String> requestHeaders = request.getHeaders(headerName);
                    Collection<String> headerValues = new ArrayList<>();
                    while (requestHeaders.hasMoreElements()) {
                        headerValues.add(requestHeaders.nextElement());
                    }
                    template.header(headerName, headerValues);
                    if (noJwt && WebConstants.HEADER_INTERNAL_JWT.equals(headerName)) {
                        noJwt = false;
                    }
                }
            }
            if (noJwt) { // 没有JWT则构建JWT传递
                String token = generateJwt();
                if (token != null) {
                    template.header(WebConstants.HEADER_INTERNAL_JWT, token);
                } else { // 确保存在JWT头信息，以便于判断是否内部RPC
                    template.header(WebConstants.HEADER_INTERNAL_JWT, Strings.EMPTY);
                }
            }
        }
    }

    private String generateJwt() {
        if (this.internalJwtStrategy != null) {
            UserSpecificDetails<?> userDetails = SecurityUtil.getAuthorizedUserDetails();
            if (userDetails != null) {
                long expiredTimeMillis = System.currentTimeMillis() + this.internalJwtStrategy
                        .getExpiredIntervalSeconds() * 1000;
                return JWT.create()
                        .withExpiresAt(new Date(expiredTimeMillis))
                        .withAudience(userDetails.getClass().getName(), JsonUtil.toJson(userDetails))
                        .sign(Algorithm.HMAC256(this.internalJwtStrategy.getSecretKey()));
            }
        }
        return null;
    }

}
