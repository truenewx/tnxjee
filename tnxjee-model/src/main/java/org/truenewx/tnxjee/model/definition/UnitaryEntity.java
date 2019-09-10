package org.truenewx.tnxjee.model.definition;

import java.io.Serializable;

/**
 * 具有单一标识属性的实体
 *
 * @author jianglei
 * @param <K> 标识类型
 */
public interface UnitaryEntity<K extends Serializable> extends Entity {

}
