package org.truenewx.tnxjee.model.definition.relation;

import java.io.Serializable;

import org.truenewx.tnxjee.model.definition.Entity;

/**
 * 关系模型
 *
 * @author jianglei
 * @param <L> 左标识类型
 * @param <R> 右标识类型
 */
public interface Relation<L extends Serializable, R extends Serializable>
        extends Entity, Serializable {

    /**
     *
     * @return 左标识
     */
    L getLeftId();

    /**
     *
     * @return 右标识
     */
    R getRightId();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}