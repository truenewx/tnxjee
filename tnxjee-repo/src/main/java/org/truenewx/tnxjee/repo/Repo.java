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
     * 保存指定实体对象
     *
     * @param <S>    实际实体类型
     * @param entity 实体对象
     * @return 保存后的实体对象，使用者应该用返回的新实体对象替换原来的实体对象
     */
    <S extends T> S save(S entity);

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
    long count();

    /**
     * 获取实体的所有数据，一般用于单元测试，请谨慎使用
     *
     * @return 当前实体的所有数据
     */
    Iterable<T> findAll();

}
