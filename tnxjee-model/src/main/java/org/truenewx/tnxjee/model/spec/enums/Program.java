package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 程序类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum Program {

    @Caption("网页")
    @EnumValue("W")
    WEB,

    @Caption("应用")
    @EnumValue("A")
    APP;

}
