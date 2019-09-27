package org.truenewx.tnxjee.repo.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.repo.Repo;
import org.truenewx.tnxjee.repo.support.RepoFactory;

/**
 * 抽象的数据提供者
 *
 * @author jianglei
 */
public abstract class AbstractDataProvider<T extends Entity> implements DataProvider<T> {
    @Autowired
    private RepoFactory repoFactory;

    private <R extends Repo<T>> R getRepo() {
        return this.repoFactory.getRepoByEntityClass(getEntityClass());
    }

    private Class<T> getEntityClass() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

    @Override
    public List<T> getDataList(DataPool pool) {
        if (getRepo().count() == 0) {
            init(pool);
        }
        return CollectionUtil.toList(getRepo().findAll());
    }

    protected final void save(T entity) {
        getRepo().save(entity);
    }

    protected abstract void init(DataPool pool);

    @Override
    public void clear() {
        getRepo().deleteAll();
    }

}
