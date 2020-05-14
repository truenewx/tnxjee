package org.truenewx.tnxjee.service.exception;

/**
 * 鉴权失败异常
 */
public class AuthenticationFailureException extends BusinessException {

    private static final long serialVersionUID = -7826129157270679401L;

    public AuthenticationFailureException() {
        super("error.service.security.authentication_failure");
    }

}
