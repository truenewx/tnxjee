package org.truenewx.tnxjee.model.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.model.UnitaryEntity;

/**
 * 单体，用id作为标识属性的实体
 *
 * @author jianglei
 * @param <K> 标识类型
 */
public interface Unity<K extends Serializable> extends UnitaryEntity<K> {
    /**
     * 获取标识
     *
     * @return 标识，唯一表示一个单体
     */
    K getId();
}
