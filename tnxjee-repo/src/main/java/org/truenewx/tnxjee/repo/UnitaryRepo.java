package org.truenewx.tnxjee.repo;

import java.io.Serializable;

import org.truenewx.tnxjee.model.definition.UnitaryEntity;

/**
 * 具有单一标识的实体的数据访问仓库
 *
 * @author jianglei
 * @param <T> 实体类型
 * @param <K> 标识类型
 */
public interface UnitaryRepo<T extends UnitaryEntity<K>, K extends Serializable> extends Repo<T> {

    /**
     * 递增指定单体的指定数值属性值
     *
     * @param key          单体标识
     * @param propertyName 数值属性名
     * @param step         递增的值，为负值即表示递减
     * @param limit        增减后允许的最大/最小值，设定以避免数值超限
     * @return 单体
     */
    T increaseNumber(K key, String propertyName, Number step, Number limit);

}