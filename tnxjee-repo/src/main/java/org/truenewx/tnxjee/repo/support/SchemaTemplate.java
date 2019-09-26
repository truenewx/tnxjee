package org.truenewx.tnxjee.repo.support;

import java.io.Serializable;
import java.util.List;

import org.truenewx.tnxjee.model.core.Entity;

/**
 * 数据库模式访问模板
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SchemaTemplate {

    /**
     * @return 数据库模式名称
     */
    String getSchema();

    /**
     * @return 管理的所有实体类型集合
     */
    Iterable<Class<?>> getEntityClasses();

    /**
     * 获取指定实体类型指定标识的对象
     *
     * @param entityClass 实体类型
     * @param key         标识
     * @return 实体对象
     */
    <T extends Entity> T find(Class<T> entityClass, Serializable key);

    <T extends Entity> List<T> findAll(Class<T> entityClass);

    long countAll(Class<?> entityClass);

    <T extends Entity> T save(T entity);

    void delete(Entity entity);

    void deleteAll(Class<?> entityClass);

}
