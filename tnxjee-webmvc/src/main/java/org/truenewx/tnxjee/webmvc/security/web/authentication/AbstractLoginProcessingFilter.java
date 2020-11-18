package org.truenewx.tnxjee.webmvc.security.web.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 抽象的登录进程过滤器
 */
public abstract class AbstractLoginProcessingFilter extends AbstractAuthenticationProcessingFilter
        implements LoginProcessingFilter {

    protected AbstractLoginProcessingFilter(String defaultFilterProcessesUrl, ApplicationContext context) {
        super(defaultFilterProcessesUrl);
        LoginProcessingFilter.init(this, context);
    }

    @Override
    public AuthenticationSuccessHandler getSuccessHandler() {
        return super.getSuccessHandler();
    }

    @Override
    public AuthenticationFailureHandler getFailureHandler() {
        return super.getFailureHandler();
    }

    protected void setDetails(HttpServletRequest request, AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

}
