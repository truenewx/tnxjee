package org.truenewx.tnxjee.repo;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;

/**
 * 从属单体的数值属性可递增
 *
 * @author jianglei
 */
public interface OwnedUnityNumberIncreasable<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable> {

    /**
     * 递增指定单体的指定数值属性值
     *
     * @param owner        所属者
     * @param id           单体标识
     * @param propertyName 数值属性名
     * @param step         递增的值，为负值即表示递减
     * @param limit        增减后允许的最大/最小值，设定以避免数值超限
     * @return 单体
     */
    T increaseNumber(O owner, K id, String propertyName, Number step, Number limit);

}
