package org.truenewx.tnxjee.repo;

import org.truenewx.tnxjee.model.entity.UnitaryEntity;

import java.io.Serializable;

/**
 * 单一标识实体的数据访问仓库
 *
 * @param <T> 实体类型
 * @param <K> 标识类型
 * @author jianglei
 */
public interface UnitaryEntityRepo<T extends UnitaryEntity<K>, K extends Serializable>
        extends Repo<T> {
    /**
     * 递增指定实体的指定数值属性值
     *
     * @param key          单体标识
     * @param propertyName 数值属性名
     * @param step         递增的值，为负值即表示递减
     * @param limit        增减后允许的最大/最小值，设定以避免数值超限
     * @return 单体
     */
    <N extends Number> T increaseNumber(K key, String propertyName, N step, N limit);
}