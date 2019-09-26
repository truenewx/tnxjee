package org.truenewx.tnxjee.repo.jpa.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.repo.support.SchemaTemplate;
import org.truenewx.tnxjee.repo.util.RepoUtil;

/**
 * JPA的数据库模式访问模板
 *
 * @author jianglei
 */
public class JpaSchemaTemplate implements SchemaTemplate {

    private String schema = RepoUtil.DEFAULT_SCHEMA_NAME;
    private EntityManager entityManager;
    private boolean nativeMode;

    public JpaSchemaTemplate(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public JpaSchemaTemplate(String schema, EntityManager entityManager) {
        Assert.notNull(schema, "schema must not be null");
        Assert.notNull(entityManager, "entityManager must not be null");
        this.schema = schema;
        this.entityManager = entityManager;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public Iterable<Class<?>> getEntityClasses() {
        List<Class<?>> entityClasses = new ArrayList<>();
        this.entityManager.getMetamodel().getManagedTypes().forEach(type -> {
            entityClasses.add(type.getJavaType());
        });
        return entityClasses;
    }

    @Override
    public <T extends Entity> T find(Class<T> entityClass, Serializable key) {
        return this.entityManager.find(entityClass, key);
    }

    @Override
    public <T extends Entity> List<T> findAll(Class<T> entityClass) {
        return list("from " + entityClass.getName());
    }

    @Override
    public long countAll(Class<?> entityClass) {
        return count("select count(*) from " + entityClass.getName());
    }

    @Override
    public <T extends Entity> T save(T entity) {
        this.entityManager.persist(entity);
        return entity;
    }

    @Override
    public void delete(Entity entity) {
        this.entityManager.remove(entity);
    }

    @Override
    public void deleteAll(Class<?> entityClass) {
        update("delete from " + entityClass.getName());
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public PersistentClass getPersistentClass(String entityName) {
        return ((MetamodelImplementor) this.entityManager.getMetamodel()).getTypeConfiguration()
                .getMetadataBuildingContext().getMetadataCollector().getEntityBinding(entityName);
    }

    /**
     * @return SQL方式的访问模板
     */
    public SchemaTemplate getNative() {
        JpaSchemaTemplate template = new JpaSchemaTemplate(this.schema, this.entityManager);
        template.nativeMode = true;
        return template;
    }

    public void flush() {
        this.entityManager.flush();
    }

    public void refresh(Entity entity) {
        this.entityManager.refresh(entity);
    }

    /**
     * 非分页查询
     */
    public <T> List<T> list(CharSequence ql, String paramName, Object paramValue) {
        return list(ql, paramName, paramValue, 0, 0);
    }

    /**
     * 非分页查询
     */
    public <T> List<T> list(CharSequence ql, Map<String, ?> params) {
        return list(ql, params, 0, 0);
    }

    /**
     * 非分页查询
     */
    public <T> List<T> list(CharSequence ql, List<?> params) {
        return list(ql, params, 0, 0);
    }

    /**
     * 非分页查询
     */
    public <T> List<T> list(CharSequence ql) {
        return list(ql, (Map<String, ?>) null);
    }

    public <T> T first(CharSequence ql, String paramName, Object paramValue) {
        List<T> list = list(ql, paramName, paramValue, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public <T> T first(CharSequence ql, Map<String, ?> params) {
        List<T> list = list(ql, params, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public <T> T first(CharSequence ql, List<?> params) {
        List<T> list = list(ql, params, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public <T> T first(CharSequence ql) {
        return first(ql, (Map<String, ?>) null);
    }

    public long count(CharSequence ql, String paramName, Object paramValue) {
        Number value = first(ql, paramName, paramValue);
        return value == null ? 0 : value.longValue();
    }

    public long count(CharSequence ql, Map<String, ?> params) {
        Number value = first(ql, params);
        return value == null ? 0 : value.longValue();
    }

    public long count(CharSequence ql, List<?> params) {
        Number value = first(ql, params);
        return value == null ? 0 : value.longValue();
    }

    public long count(CharSequence ql) {
        return count(ql, (Map<String, ?>) null);
    }

    private Query createQuery(CharSequence ql) {
        if (this.nativeMode) {
            return this.entityManager.createNativeQuery(ql.toString());
        }
        return this.entityManager.createQuery(ql.toString());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(CharSequence ql, String paramName, Object paramValue, int pageSize,
            int pageNo) {
        Query query = createQuery(ql);
        applyParamToQuery(query, paramName, paramValue);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(CharSequence ql, Map<String, ?> params, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(CharSequence ql, List<?> params, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.getResultList();
    }

    /**
     * 分页查询
     */
    public <T> List<T> list(CharSequence ql, int pageSize, int pageNo) {
        return list(ql, (Map<String, ?>) null, pageSize, pageNo);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listWithOneMore(CharSequence ql, String paramName, Object paramValue,
            int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamToQuery(query, paramName, paramValue);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listWithOneMore(CharSequence ql, Map<String, ?> params, int pageSize,
            int pageNo) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listWithOneMore(CharSequence ql, List<?> params, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.getResultList();
    }

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql       查询语句
     * @param pageSize 页大小
     * @param pageNo   页码
     * @return 查询结果
     */
    public <T> List<T> listWithOneMore(CharSequence ql, int pageSize, int pageNo) {
        return listWithOneMore(ql, (Map<String, ?>) null, pageSize, pageNo);
    }

    public int update(CharSequence ql, String paramName, Object paramValue) {
        Query query = createQuery(ql);
        applyParamToQuery(query, paramName, paramValue);
        return query.executeUpdate();
    }

    public int update(CharSequence ql, Map<String, ?> params) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        return query.executeUpdate();
    }

    public int update(CharSequence ql, List<?> params) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        return query.executeUpdate();
    }

    public int update(CharSequence ul) {
        return update(ul, (Map<String, ?>) null);
    }

    public void applyParamsToQuery(Query query, Map<String, ?> params) {
        if (params != null) {
            for (Entry<String, ?> entry : params.entrySet()) {
                applyParamToQuery(query, entry.getKey(), entry.getValue());
            }
        }
    }

    public void applyParamToQuery(Query query, String name, Object value) {
        if (value instanceof Calendar) {
            query.setParameter(name, (Calendar) value, TemporalType.TIMESTAMP);
        } else if (value instanceof Date) {
            query.setParameter(name, (Date) value, TemporalType.TIMESTAMP);
        } else {
            query.setParameter(name, value);
        }
    }

    public void applyParamsToQuery(Query query, List<?> params) {
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                applyParamToQuery(query, i, params.get(i));
            }
        }
    }

    public void applyParamToQuery(Query query, int position, Object value) {
        if (value instanceof Calendar) {
            query.setParameter(position, (Calendar) value, TemporalType.TIMESTAMP);
        } else if (value instanceof Date) {
            query.setParameter(position, (Date) value, TemporalType.TIMESTAMP);
        } else {
            query.setParameter(position, value);
        }
    }

    public void applyPagingToQuery(Query query, int pageSize, int pageNo, boolean oneMore) {
        if (pageSize > 0) { // 用页大小判断是否分页查询
            if (pageNo <= 0) { // 页码最小为1
                pageNo = 1;
            }
            query.setFirstResult(pageSize * (pageNo - 1));
            query.setMaxResults(oneMore ? (pageSize + 1) : pageSize);
        }
    }

}
