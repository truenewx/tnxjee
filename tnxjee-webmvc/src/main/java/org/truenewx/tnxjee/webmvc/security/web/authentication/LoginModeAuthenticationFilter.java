package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 支持多种登录方式的认证过滤器。
 */
// 多种登录方式用一个过滤器处理的好处是登录处理URL可以与登录表单URL保持一致，从而有利于登录失败后的处理。
public class LoginModeAuthenticationFilter extends LoginAuthenticationFilter {

    public static final String PARAMETER_LOGIN_MODE = "loginMode";

    private AuthenticationTokenBuilder<AbstractAuthenticationToken> defaultTokenBuilder;
    private Map<String, AuthenticationTokenBuilder<AbstractAuthenticationToken>> tokenBuilderMapping = new HashMap<>();

    public LoginModeAuthenticationFilter(ApplicationContext context) {
        super(context);

        this.tokenBuilderMapping.clear();
        context.getBeansOfType(AuthenticationTokenBuilder.class).forEach((id, builder) -> {
            String loginMode = builder.getLoginMode();
            if (loginMode == null) {
                this.defaultTokenBuilder = builder;
            } else {
                this.tokenBuilderMapping.put(loginMode, builder);
            }
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String loginMode = obtainLoginMode(request);
        AuthenticationTokenBuilder<AbstractAuthenticationToken> builder;
        if (loginMode == null) {
            builder = this.defaultTokenBuilder;
        } else {
            builder = this.tokenBuilderMapping.get(loginMode);
        }
        if (builder != null) {
            AbstractAuthenticationToken authRequest = builder.buildAuthenticationToken(request);
            setDetails(request, authRequest);
            return getAuthenticationManager().authenticate(authRequest);
        }
        // 找不到匹配登录方式的构建器，则采用父类的用户名密码登录方式
        return super.attemptAuthentication(request, response);
    }

    public String obtainLoginMode(HttpServletRequest request) {
        return request.getParameter(PARAMETER_LOGIN_MODE);
    }

    protected void setDetails(HttpServletRequest request, AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

}
