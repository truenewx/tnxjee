package org.truenewx.tnxjee.repo;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 单体数据访问仓库
 *
 * @param <T> 单体类型
 * @param <K> 单体标识类型
 * @author jianglei
 */
public interface UnityRepo<T extends Unity<K>, K extends Serializable> extends Repo<T> {

    /**
     * 递增指定实体的指定数值属性值
     *
     * @param id           单体标识
     * @param propertyName 数值属性名
     * @param step         递增的值，为负值即表示递减
     * @param limit        增减后允许的最大/最小值，设定以避免数值超限
     * @return 单体
     */
    default <N extends Number> T increaseNumber(K id, String propertyName, N step, N limit) {
        throw new UnsupportedOperationException();
    }

}
