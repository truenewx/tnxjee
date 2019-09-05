package org.truenewx.tnxjee.repo.support;

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

}
