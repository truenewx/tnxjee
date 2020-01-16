package org.truenewx.tnxjee.repo;

import java.io.Serializable;
import java.util.Optional;

import org.truenewx.tnxjee.model.entity.relation.Relation;

/**
 * 关系数据访问仓库
 *
 * @author jianglei
 * @param <T> 关系类型
 * @param <L> 左标识类型
 * @param <R> 右标识类型
 */
public interface RelationRepo<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends Repo<T> {

    Optional<T> findById(L leftId, R rightId);

    boolean exists(L leftId, R rightId);

}
