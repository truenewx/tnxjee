package org.truenewx.tnxjee.web.controller.spring.security.user;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户鉴定服务
 */
public interface UserAuthenticationService extends UserDetailsService, AuthenticationProvider {

    @Override
    default boolean supports(Class<?> authentication) {
        return true;
    }

}
