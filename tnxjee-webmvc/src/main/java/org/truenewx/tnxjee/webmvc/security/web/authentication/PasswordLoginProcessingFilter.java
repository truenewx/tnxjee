package org.truenewx.tnxjee.webmvc.security.web.authentication;

import org.springframework.context.ApplicationContext;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 密码登录进程过滤器
 */
public class PasswordLoginProcessingFilter extends UsernamePasswordAuthenticationFilter
        implements LoginProcessingFilter {

    public PasswordLoginProcessingFilter(ApplicationContext context) {
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

}
