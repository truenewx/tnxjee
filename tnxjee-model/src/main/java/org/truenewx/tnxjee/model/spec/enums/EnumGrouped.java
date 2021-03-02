package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.model.spec.Grouped;

/**
 * 按枚举类型分组的
 *
 * @param <E> 分组枚举类型
 */
public interface EnumGrouped<E extends Enum<?>> extends Grouped<E> {
}
