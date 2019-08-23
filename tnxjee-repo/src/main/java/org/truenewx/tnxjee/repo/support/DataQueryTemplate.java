package org.truenewx.tnxjee.repo.support;

import java.util.List;
import java.util.Map;

import org.truenewx.tnxjee.core.util.CollectionUtil;

/**
 * 数据查询模板
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class DataQueryTemplate {

    /**
     * 非分页查询
     */
    public final <T> List<T> list(CharSequence ql, String paramName, Object paramValue) {
        return list(ql, paramName, paramValue, 0, 0);
    }

    /**
     * 非分页查询
     */
    public final <T> List<T> list(CharSequence ql, Map<String, ?> params) {
        return list(ql, params, 0, 0);
    }

    /**
     * 非分页查询
     */
    public final <T> List<T> list(CharSequence ql, List<?> params) {
        return list(ql, params, 0, 0);
    }

    /**
     * 非分页查询
     */
    public final <T> List<T> list(CharSequence ql) {
        return list(ql, (Map<String, ?>) null);
    }

    public final <T> T first(CharSequence ql, String paramName, Object paramValue) {
        List<T> list = list(ql, paramName, paramValue, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public final <T> T first(CharSequence ql, Map<String, ?> params) {
        List<T> list = list(ql, params, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public final <T> T first(CharSequence ql, List<?> params) {
        List<T> list = list(ql, params, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public final <T> T first(CharSequence ql) {
        return first(ql, (Map<String, ?>) null);
    }

    public final int count(CharSequence ql, String paramName, Object paramValue) {
        Number value = first(ql, paramName, paramValue);
        return value == null ? 0 : value.intValue();
    }

    public final int count(CharSequence ql, Map<String, ?> params) {
        Number value = first(ql, params);
        return value == null ? 0 : value.intValue();
    }

    public final int count(CharSequence ql, List<?> params) {
        Number value = first(ql, params);
        return value == null ? 0 : value.intValue();
    }

    public final int count(CharSequence ql) {
        return count(ql, (Map<String, ?>) null);
    }

    /**
     * 分页查询
     */
    public abstract <T> List<T> list(CharSequence ql, String paramName, Object paramValue,
            int pageSize, int pageNo);

    /**
     * 分页查询
     */
    public abstract <T> List<T> list(CharSequence ql, Map<String, ?> params, int pageSize,
            int pageNo);

    /**
     * 分页查询
     */
    public abstract <T> List<T> list(CharSequence ql, List<?> params, int pageSize, int pageNo);

    /**
     * 分页查询
     */
    public final <T> List<T> list(CharSequence ql, int pageSize, int pageNo) {
        return list(ql, (Map<String, ?>) null, pageSize, pageNo);
    }

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql         查询语句
     * @param paramName  参数名
     * @param paramValue 参数值
     * @param pageSize   页大小
     * @param pageNo     页码
     * @return 查询结果
     */
    public abstract <T> List<T> listWithOneMore(CharSequence ql, String paramName,
            Object paramValue, int pageSize, int pageNo);

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql       查询语句
     * @param params   参数映射集
     * @param pageSize 页大小
     * @param pageNo   页码
     * @return 查询结果
     */
    public abstract <T> List<T> listWithOneMore(CharSequence ql, Map<String, ?> params,
            int pageSize, int pageNo);

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql       查询语句
     * @param params   参数集
     * @param pageSize 页大小
     * @param pageNo   页码
     * @return 查询结果
     */
    public abstract <T> List<T> listWithOneMore(CharSequence ql, List<?> params, int pageSize,
            int pageNo);

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql       查询语句
     * @param pageSize 页大小
     * @param pageNo   页码
     * @return 查询结果
     */
    public final <T> List<T> listWithOneMore(CharSequence ql, int pageSize, int pageNo) {
        return listWithOneMore(ql, (Map<String, ?>) null, pageSize, pageNo);
    }

}
