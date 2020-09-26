package org.truenewx.tnxjee.webmvc.security.core;

import org.springframework.security.core.AuthenticationException;

/**
 * 鉴权失败异常
 */
public class AuthenticationFailureException extends AuthenticationException {

    private static final long serialVersionUID = -7826129157270679401L;

    public AuthenticationFailureException() {
        super("error.service.security.authentication_failure");
    }

}
