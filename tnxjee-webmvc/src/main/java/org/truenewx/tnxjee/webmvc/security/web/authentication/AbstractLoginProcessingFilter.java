package org.truenewx.tnxjee.webmvc.security.web.authentication;

import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * 抽象的登录进程过滤器
 */
public abstract class AbstractLoginProcessingFilter extends AbstractAuthenticationProcessingFilter
        implements LoginProcessingFilter {

    protected AbstractLoginProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public AuthenticationFailureHandler getFailureHandler() {
        return super.getFailureHandler();
    }

}
