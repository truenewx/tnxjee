package org.truenewx.tnxjee.repo;

import java.io.Serializable;

import org.truenewx.tnxjee.model.definition.relation.Relation;

/**
 * 关系的数值属性可递增
 *
 * @author jianglei
 */
public interface RelationNumberIncreasable<T extends Relation<L, R>, L extends Serializable, R extends Serializable> {
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
