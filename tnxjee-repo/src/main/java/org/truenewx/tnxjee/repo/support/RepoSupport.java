package org.truenewx.tnxjee.repo.support;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.repo.Repo;

/**
 * 数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class RepoSupport<T extends Entity> implements Repo<T> {

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private SchemaTemplateFactory schemaTemplateFactory;

    /**
     * 获取实体类型<br/>
     * 默认实现通过反射机制获取，子类可覆写直接返回具体实体的类型以优化性能
     *
     * @return 实体类型
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {
        return (Class<T>) ClassUtil.getActualGenericType(getClass(), 0);
    }

    protected <R extends Repository<T, ?>> R getRepository() {
        return this.repositoryFactory.getRepositoryByEntityClass(getEntityClass());
    }

    protected SchemaTemplate getSchemaTemplate() {
        return this.schemaTemplateFactory.getSchemaTemplate(getEntityClass());
    }

    @Override
    public <S extends T> S save(S entity) {
        return getSchemaTemplate().save(entity);
    }

    @Override
    public void delete(T entity) {
        getSchemaTemplate().delete(entity);
    }

    @Override
    public long count() {
        return getSchemaTemplate().countAll(getEntityClass());
    }

    @Override
    public Iterable<T> findAll() {
        return getSchemaTemplate().findAll(getEntityClass());
    }

    protected Class<?> getPropertyClass(String propertyName) {
        Field field = ClassUtil.findField(getEntityClass(), propertyName);
        return field == null ? null : field.getType();
    }

}
