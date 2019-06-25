package org.truenewx.tnxjee.core.util.tuple;

import org.truenewx.tnxjee.core.util.function.FuncHashCode;
import org.truenewx.tnxjee.core.util.function.PredEqual;

/**
 * 对称二元体，其左右元顺序不敏感，(a,b)等同于(b,a)
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T> 元素类型
 */
public class SymplexBinary<T> extends Binary<T, T> {

    /**
     * @param left  左元
     * @param right 右元
     */
    public SymplexBinary(T left, T right) {
        super(left, right);
    }

    /**
     * 反转左右元
     *
     * @author jianglei
     */
    public void reverse() {
        T left = getLeft();
        setLeft(getRight());
        setRight(left);
    }

    @Override
    public int hashCode() {
        return FuncHashCode.INSTANCE.apply(getLeft()) * FuncHashCode.INSTANCE.apply(getRight());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SymplexBinary<T> other = (SymplexBinary<T>) obj;
        return (PredEqual.INSTANCE.test(getLeft(), other.getLeft())
                && PredEqual.INSTANCE.test(getRight(), other.getRight()))
                || (PredEqual.INSTANCE.test(getLeft(), other.getRight())
                        && PredEqual.INSTANCE.test(getRight(), other.getLeft()));
    }

    @Override
    public SymplexBinary<T> clone() {
        return new SymplexBinary<>(getLeft(), getRight());
    }

}
