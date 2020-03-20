package org.truenewx.tnxjee.core.util.function;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * 函数：计算hashCode
 *
 * @author jianglei
 * 
 */
public class FuncHashCode implements Function<Object, Integer> {
    /**
     * 单例
     */
    public static final FuncHashCode INSTANCE = new FuncHashCode();
    /**
     * 质数
     */
    public static final int PRIME = 31;
    /**
     * 布尔值false的hashCode
     */
    private static final int BOOLEAN_FALSE_CODE = 1237;
    /**
     * 布尔值true的hashCode
     */
    private static final int BOOLEAN_TRUE_CODE = 1231;

    /**
     * 私有构造函数
     */
    private FuncHashCode() {
    }

    @Override
    public Integer apply(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Boolean) {
            boolean b = (Boolean) obj;
            if (b) {
                return BOOLEAN_TRUE_CODE;
            } else {
                return BOOLEAN_FALSE_CODE;
            }
        }
        if (obj instanceof Class) { // Class类未实现hashCode()，转为调用完整类名字符串的hashCode()
            return ((Class<?>) obj).getName().hashCode();
        }
        if (obj instanceof Iterable) {
            return evaluateIterable((Iterable<?>) obj);
        }
        if (obj instanceof Map) {
            return evaluateIterable(((Map<?, ?>) obj).entrySet());
        }
        if (obj instanceof Object[]) {
            return evaluateArray((Object[]) obj);
        }
        if (obj instanceof Entry<?, ?>) {
            Entry<?, ?> entry = (Entry<?, ?>) obj;
            return apply(entry.getKey()) + PRIME * apply(entry.getValue());
        }
        return obj.hashCode();
    }

    /**
     * 计算可迭代集合的hashCode值
     *
     * @param iterable 可迭代集合
     * @return hashCode值
     */
    private int evaluateIterable(Iterable<?> iterable) {
        int result = 1;
        for (Object obj : iterable) {
            result = PRIME * result + apply(obj);
        }
        return result;
    }

    /**
     * 计算数组的hashCode值
     *
     * @param array 数组
     * @return hashCode值
     */
    private int evaluateArray(Object[] array) {
        int result = 1;
        for (Object obj : array) {
            result = PRIME * result + apply(obj);
        }
        return result;
    }

}
