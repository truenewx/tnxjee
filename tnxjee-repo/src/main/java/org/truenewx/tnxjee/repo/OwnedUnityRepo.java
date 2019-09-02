package org.truenewx.tnxjee.repo;

import java.io.Serializable;

import org.truenewx.tnxjee.model.definition.unity.OwnedUnity;

/**
 * 从属单体的数据访问仓库
 *
 * @author jianglei
 * @param <T> 单体类型
 * @param <K> 标识类型
 * @param <O> 所属者类型
 */
public interface OwnedUnityRepo<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends UnityRepo<T, K> {
    /**
     * 获取指定所属者下的单体个数
     *
     * @param owner 所属者
     * @return 指定所属者下的单体个数
     */
    int countByOwner(O owner);

    /**
     * 获取指定所属者下指定标识的单体
     *
     * @param owner 所属者
     * @param id    单体标识
     * @return 单体
     */
    T findByOwnerAndId(O owner, K id);

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