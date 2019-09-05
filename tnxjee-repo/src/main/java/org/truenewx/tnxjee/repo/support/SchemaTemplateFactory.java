package org.truenewx.tnxjee.repo.support;

/**
 * 数据库模式访问模板工厂
 *
 * @author jianglei
 */
public interface SchemaTemplateFactory {

    SchemaTemplate getSchemaTemplate(Class<?> entityClass);

}
