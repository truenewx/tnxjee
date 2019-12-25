package org.truenewx.tnxjee.web.controller.spring.security.authentication;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.truenewx.tnxjee.model.user.security.UserSpecificDetails;

/**
 * 用户特性细节的认证令牌
 */
public class UserSpecificDetailsAuthenticationToken extends RememberMeAuthenticationToken { // 继承以获得RememberMe能力

    private static final long serialVersionUID = 5719790668377346866L;

    public UserSpecificDetailsAuthenticationToken(UserSpecificDetails<?> user) {
        super(user.getIdentity().toString(), user, user.getAuthorities());
    }

}
