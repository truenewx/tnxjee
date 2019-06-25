package org.truenewx.tnxjee.core.enums;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.annotation.Caption;

/**
 * 单位类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum UnitType {

    @Caption(Strings.EMPTY)
    NONE,

    @Caption("时间")
    TIME,

    @Caption("重量")
    WEIGHT,

    @Caption("长度")
    LENGTH,

    @Caption("面积")
    AREA,

    @Caption("体积")
    VOLUME,

    @Caption("数量")
    QUANTITY;

}
