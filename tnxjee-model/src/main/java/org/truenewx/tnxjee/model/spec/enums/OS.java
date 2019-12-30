package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 操作系统类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum OS {

    @Caption("Windows")
    @EnumValue("W")
    WINDOWS,

    @Caption("安卓")
    @EnumValue("A")
    ANDROID,

    @Caption("苹果")
    @EnumValue("M")
    MAC,

    @Caption("所有")
    @EnumValue("L")
    ALL;
}
