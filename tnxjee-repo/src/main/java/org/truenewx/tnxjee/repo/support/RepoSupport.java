package org.truenewx.tnxjee.repo.support;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
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
    private DataAccessTemplateFactory accessTemplateFactory;

    /**
     * 获取实体类型<br/>
     * 默认实现通过反射机制获取，子类可覆写直接返回具体实体的类型以优化性能
     *
     * @return 实体类型
     */
    protected Class<T> getEntityClass() {
        // 用指定类型的局部变量，以更好地类型转换，直接类型转换返回在IDEA中会编译失败
        Class<T> entityClass = ClassUtil.getActualGenericType(getClass(), 0);
        return entityClass;
    }

    protected <R extends CrudRepository<T, ?>> R getRepository() {
        R repository = this.repositoryFactory.getRepositoryByEntityClass(getEntityClass());
        if (repository == null) {
            repository = buildDefaultRepository();
            if (repository != null) {
                this.repositoryFactory.putRepositoryIfAbsent(getEntityClass(), repository);
            }
        }
        return repository;
    }

    protected abstract <R extends CrudRepository<T, ?>> R buildDefaultRepository();

    protected DataAccessTemplate getAccessTemplate() {
        return this.accessTemplateFactory.getDataAccessTemplate(getEntityClass());
    }

    @Override
    public <S extends T> S save(S entity) {
        return getRepository().save(entity);
    }

    @Override
    public void delete(T entity) {
        getRepository().delete(entity);
    }

    @Override
    public void deleteAll() {
        getRepository().deleteAll();
    }

    @Override
    public long count() {
        return getRepository().count();
    }

    @Override
    public Iterable<T> findAll() {
        return getRepository().findAll();
    }

    protected Class<?> getPropertyClass(String propertyName) {
        Field field = ClassUtil.findField(getEntityClass(), propertyName);
        return field == null ? null : field.getType();
    }

}
