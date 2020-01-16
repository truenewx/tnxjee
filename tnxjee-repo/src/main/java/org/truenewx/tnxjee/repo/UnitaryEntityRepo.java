package org.truenewx.tnxjee.repo;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.UnitaryEntity;

/**
 * 单一标识实体的数据访问仓库
 *
 * @author jianglei
 * @param <T> 实体类型
 * @param <K> 标识类型
 */
public interface UnitaryEntityRepo<T extends UnitaryEntity<K>, K extends Serializable>
        extends Repo<T> {

}