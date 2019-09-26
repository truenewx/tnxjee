package org.truenewx.tnxjee.repo.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.repo.support.SchemaTemplate;
import org.truenewx.tnxjee.repo.support.SchemaTemplateFactory;

/**
 * 抽象的数据提供者
 *
 * @author jianglei
 */
public abstract class AbstractDataProvider<T extends Entity> implements DataProvider<T> {
    @Autowired
    private SchemaTemplateFactory schemaTemplateFactory;

    private SchemaTemplate getSchemaTemplate() {
        return this.schemaTemplateFactory.getSchemaTemplate(getEntityClass());
    }

    private Class<T> getEntityClass() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

    @Override
    public List<T> getDataList(DataPool pool) {
        if (getSchemaTemplate().countAll(getEntityClass()) == 0) {
            init(pool);
        }
        return getSchemaTemplate().findAll(getEntityClass());
    }

    protected final void save(T entity) {
        getSchemaTemplate().save(entity);
    }

    protected abstract void init(DataPool pool);

    @Override
    public void clear() {
        getSchemaTemplate().deleteAll(getEntityClass());
    }

}
