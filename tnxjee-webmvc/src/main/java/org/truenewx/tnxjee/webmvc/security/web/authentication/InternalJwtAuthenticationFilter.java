package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import org.truenewx.tnxjee.core.config.InternalJwtConfiguration;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.security.authentication.UserSpecificDetailsAuthenticationToken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * 内部JWT鉴定过滤器
 */
public class InternalJwtAuthenticationFilter extends GenericFilterBean {

    private JWTVerifier verifier;

    public InternalJwtAuthenticationFilter(ApplicationContext context) {
        InternalJwtConfiguration configuration = SpringUtil
                .getFirstBeanByClass(context, InternalJwtConfiguration.class);
        if (configuration != null) {
            String secretKey = configuration.getSecretKey();
            Assert.notNull(secretKey, "The secretKey of InternalJwtStrategy must be not null");
            this.verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (this.verifier != null) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext != null) {
                Authentication authentication = securityContext.getAuthentication();
                if (authentication == null) {
                    HttpServletRequest request = (HttpServletRequest) req;
                    String token = request.getHeader(WebConstants.HEADER_INTERNAL_JWT);
                    if (StringUtils.isNotBlank(token)) {
                        try {
                            DecodedJWT jwt = this.verifier.verify(token);
                            String json = CollectionUtil.getFirst(jwt.getAudience(), null);
                            if (StringUtils.isNotBlank(json)) {
                                UserSpecificDetails<?> details = (UserSpecificDetails<?>) JsonUtil
                                        .json2Bean(json, UserSpecificDetails.class);
                                Authentication authResult = new UserSpecificDetailsAuthenticationToken(details);
                                securityContext.setAuthentication(authResult);
                            }
                        } catch (Exception e) { // 出现任何错误均只打印日志，视为没有授权
                            LogUtil.error(getClass(), e);
                        }
                    }
                }
            }
        }

        chain.doFilter(req, res);
    }

}
