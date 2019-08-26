package org.truenewx.tnxjee.model.user;

/**
 * 可获取用户标识的
 *
 * @author jianglei
 */
public interface UserIdentifiable<I extends UserIdentity> {

    /**
     *
     * @return 用户标识
     */
    I getUserIdentity();

}
