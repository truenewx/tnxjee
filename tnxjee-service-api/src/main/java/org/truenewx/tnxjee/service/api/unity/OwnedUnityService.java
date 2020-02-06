package org.truenewx.tnxjee.service.api.unity;

import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;

import java.io.Serializable;

/**
 * 从属单体服务
 *
 * @param <T> 单体类型
 * @param <K> 标识类型
 * @param <O> 所属者类型
 * @author jianglei
 */
public interface OwnedUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends UnityService<T, K> {
    /**
     * 查找指定所属者和标识的单体，如果找不到则返回null
     *
     * @param owner 所属者
     * @param id    单体标识
     * @return 单体
     */
    T find(O owner, K id);

    /**
     * 加载指定所属者和标识的单体，如果没找到则抛出异常
     *
     * @param owner 所属者
     * @param id    标识
     * @return 单体
     */
    T load(O owner, K id);

    /**
     * 删除从属单体
     *
     * @param owner 所属者
     * @param id    要删除的单体的标识
     * @return 是否成功删除，如果要删除的单体本就不存在则返回false
     */
    boolean delete(O owner, K id);
}
