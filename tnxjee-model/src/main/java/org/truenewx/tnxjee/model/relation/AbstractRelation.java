package org.truenewx.tnxjee.model.relation;

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
public abstract class AbstractRelation<L extends Serializable, R extends Serializable>
        implements Relation<L, R> {

    private static final long serialVersionUID = -9071339607117025280L;

    @Override
    public final int hashCode() {
        return FuncHashCode.INSTANCE.apply(new Object[] { getLeftId(), getRightId() });
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final Relation<L, R> other = (Relation<L, R>) obj;
        return PredEqual.INSTANCE.test(getLeftId(), other.getLeftId())
                && PredEqual.INSTANCE.test(getRightId(), other.getRightId());
    }

}
