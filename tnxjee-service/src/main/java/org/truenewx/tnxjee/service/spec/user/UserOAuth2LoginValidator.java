package org.truenewx.tnxjee.service.spec.user;

import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 用户OAuth2登录校验器
 */
public interface UserOAuth2LoginValidator {

    UserSpecificDetails<?> validateOAuth2Login(String userType, String scope, Object userModel);

}
