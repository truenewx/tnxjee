package org.truenewx.tnxjee.service.spec.user;

import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 用户密码登录校验器
 */
public interface UserPasswordLoginValidator {

    UserSpecificDetails<?> validatePasswordLogin(String userType, String scope, String username, String password);

}
