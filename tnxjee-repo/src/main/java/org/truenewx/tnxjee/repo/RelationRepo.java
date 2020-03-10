package org.truenewx.tnxjee.repo;

import org.truenewx.tnxjee.model.entity.relation.Relation;

import java.io.Serializable;
import java.util.Optional;

/**
 * 关系数据访问仓库
 *
 * @param <T> 关系类型
 * @param <L> 左标识类型
 * @param <R> 右标识类型
 * @author jianglei
 */
public interface RelationRepo<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends Repo<T> {

    Optional<T> findById(L leftId, R rightId);

    boolean exists(L leftId, R rightId);

    /**
     * 递增指定关系的指定数值属性值
     *
     * @param leftId       左标识
     * @param rightId      右标识
     * @param propertyName 数值属性名
     * @param step         递增的值，为负值即表示递减
     * @param limit        增减后允许的最大/最小值，设定以避免数值超限
     * @return 关系
     */
    T increaseNumber(L leftId, R rightId, String propertyName, Number step, Number limit);
}
