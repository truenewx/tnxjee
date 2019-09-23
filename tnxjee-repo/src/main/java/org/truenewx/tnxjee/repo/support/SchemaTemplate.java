package org.truenewx.tnxjee.repo.support;

import java.io.Serializable;

import org.truenewx.tnxjee.model.Entity;

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

}
