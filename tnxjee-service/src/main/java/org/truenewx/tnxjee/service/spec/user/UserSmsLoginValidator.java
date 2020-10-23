package org.truenewx.tnxjee.service.spec.user;

import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 用户短信登录校验器
 */
public interface UserSmsLoginValidator {

    UserSpecificDetails<?> validateSmsLogin(String userType, String scope, String mobilePhone, String verifyCode);

}
