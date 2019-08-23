package org.truenewx.tnxjee.model.definition;

import java.io.Serializable;

/**
 * 具有单一标识属性的实体
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <K> 标识类型
 */
public interface UnitaryEntity<K extends Serializable> extends Entity {

}
