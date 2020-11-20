package org.truenewx.tnxjee.webmvc.security.web.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 抽象的登录认证令牌构建器
 *
 * @param <T> 令牌类型
 */
public abstract class AbstractAuthenticationTokenBuilder<T extends AbstractAuthenticationToken>
        implements AuthenticationTokenBuilder<T> {

    private String loginMode;

    public AbstractAuthenticationTokenBuilder(String loginMode) {
        this.loginMode = loginMode;
    }

    @Override
    public String getLoginMode() {
        return this.loginMode;
    }

}
