package org.truenewx.tnxjee.core.enums;

import org.truenewx.tnxjee.core.annotation.Caption;
import org.truenewx.tnxjee.core.enums.annotation.EnumValue;

/**
 * 设备类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum Device {

    @Caption("电脑")
    @EnumValue("C")
    PC,

    @Caption("手机")
    @EnumValue("M")
    MOBILE,

    @Caption("平板")
    @EnumValue("P")
    PAD;
}
