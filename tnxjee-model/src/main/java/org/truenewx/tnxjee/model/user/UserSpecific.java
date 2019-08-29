package org.truenewx.tnxjee.model.user;

/**
 * 具有用户特性的
 *
 * @author jianglei
 */
public interface UserSpecific<I extends UserIdentity> {

    /**
     * 用户标识应该是仅用于数据传递的，可公开但不具备业务含义，人工无法识别的
     *
     * @return 用户标识
     */
    I getIdentity();

    /**
     * 用户名应该是在同一个用户类型中唯一的，可用于登录的，不对外公开的
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 显示名称应该是可公开的，具有充分的业务含义，便于人工识别的
     *
     * @return 显示名称
     */
    String getCaption();

}
