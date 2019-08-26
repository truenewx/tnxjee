package org.truenewx.tnxjee.repo;

import org.truenewx.tnxjee.model.definition.Entity;

/**
 * 数据访问仓库
 *
 * @author jianglei
 * @param <T> 实体类型
 */
public interface Repo<T extends Entity> {

    /**
     *
     * @return 实体类型
     */
    Class<T> getEntityClass();

    /**
     * 保存指定实体对象
     *
     * @param entity 实体对象
     */
    void save(T entity);

    /**
     * 删除指定实体对象
     *
     * @param entity 实体对象
     */
    void delete(T entity);

    /**
     * 获取所有实体的总数
     *
     * @return 所有实体的总数
     */
    long countAll();

    /**
     * 获取已有数据中的第一条，常用于单元测试，谨慎用于它处
     *
     * @return 第一条数据记录
     */
    T first();
}
