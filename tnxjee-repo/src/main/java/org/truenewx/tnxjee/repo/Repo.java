package org.truenewx.tnxjee.repo;

import java.util.List;

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
     * 获取实体的所有数据，一般用于单元测试，请谨慎使用
     *
     * @return 当前实体的所有数据
     */
    List<T> findAll();

    /**
     * 获取实体的第一条数据，一般在单元测试中才有意义
     *
     * @return 实体的第一条数据
     */
    T first();
}
