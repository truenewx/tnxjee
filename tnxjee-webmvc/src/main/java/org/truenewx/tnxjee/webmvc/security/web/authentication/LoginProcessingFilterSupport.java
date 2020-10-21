package org.truenewx.tnxjee.webmvc.security.web.authentication;

import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * 登录进程过滤器支持
 */
public abstract class LoginProcessingFilterSupport extends AbstractAuthenticationProcessingFilter {

    protected LoginProcessingFilterSupport(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }


}
