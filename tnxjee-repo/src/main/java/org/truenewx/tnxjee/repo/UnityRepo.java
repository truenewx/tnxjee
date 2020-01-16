package org.truenewx.tnxjee.repo;

import java.io.Serializable;
import java.util.Optional;

import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 单体数据访问仓库
 *
 * @author jianglei
 * @param <T> 单体类型
 * @param <K> 单体标识类型
 */
public interface UnityRepo<T extends Unity<K>, K extends Serializable> extends UnitaryEntityRepo<T, K> {

    Optional<T> findById(K id);

}
