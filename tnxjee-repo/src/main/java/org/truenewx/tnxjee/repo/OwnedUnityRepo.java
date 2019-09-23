package org.truenewx.tnxjee.repo;

import java.io.Serializable;

import org.truenewx.tnxjee.model.unity.OwnedUnity;

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
    long countByOwner(O owner);

    /**
     * 获取指定所属者下指定标识的单体
     *
     * @param owner 所属者
     * @param id    单体标识
     * @return 单体
     */
    T findByOwnerAndId(O owner, K id);

}
