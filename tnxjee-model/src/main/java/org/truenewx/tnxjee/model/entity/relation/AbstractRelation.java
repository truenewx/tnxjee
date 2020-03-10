package org.truenewx.tnxjee.model.entity.relation;

import java.io.Serializable;

import org.truenewx.tnxjee.core.util.function.FuncHashCode;
import org.truenewx.tnxjee.core.util.function.PredEqual;

/**
 * 抽象的关系
 *
 * @author jianglei
 * @param <L> 左标识类型
 * @param <R> 右标识类型
 */
public abstract class AbstractRelation<L extends Serializable, R extends Serializable> implements Relation<L, R> {

    public abstract <K extends RelationKey<L, R>> K getId();

    @Override
    public L getLeftId() {
        RelationKey<L, R> id = getId();
        return id == null ? null : id.getLeft();
    }

    @Override
    public R getRightId() {
        RelationKey<L, R> id = getId();
        return id == null ? null : id.getRight();
    }

    @Override
    public int hashCode() {
        return FuncHashCode.INSTANCE.apply(new Object[] { getLeftId(), getRightId() });
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Relation<L, R> other = (Relation<L, R>) obj;
        return PredEqual.INSTANCE.test(getLeftId(), other.getLeftId())
                && PredEqual.INSTANCE.test(getRightId(), other.getRightId());
    }

}