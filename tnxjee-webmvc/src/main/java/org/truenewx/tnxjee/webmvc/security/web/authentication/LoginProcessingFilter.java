package org.truenewx.tnxjee.webmvc.security.web.authentication;

import javax.servlet.Filter;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * 登录进程过滤器
 */
public interface LoginProcessingFilter extends Filter {

    AuthenticationFailureHandler getFailureHandler();

}
