package org.truenewx.tnxjee.repo.jpa.support;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.hibernate.SessionFactory;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.repo.support.DataAccessTemplate;
import org.truenewx.tnxjee.repo.util.RepoUtil;

/**
 * JPA的数据访问模板
 *
 * @author jianglei
 */
public class JpaAccessTemplate implements DataAccessTemplate {

    private String schema = RepoUtil.DEFAULT_SCHEMA_NAME;
    private EntityManagerFactory entityManagerFactory;
    private boolean nativeMode;

    public JpaAccessTemplate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public JpaAccessTemplate(String schema, EntityManagerFactory entityManagerFactory) {
        Assert.notNull(schema, "schema must not be null");
        Assert.notNull(entityManagerFactory, "entityManager must not be null");
        this.schema = schema;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public Iterable<Class<?>> getEntityClasses() {
        List<Class<?>> entityClasses = new ArrayList<>();
        this.entityManagerFactory.getMetamodel().getManagedTypes().forEach(type -> {
            entityClasses.add(type.getJavaType());
        });
        return entityClasses;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    public EntityManager getCurrentEntityManager() {
        if (this.entityManagerFactory instanceof SessionFactory) {
            SessionFactory sessionFactory = (SessionFactory) this.entityManagerFactory;
            return sessionFactory.getCurrentSession();
        }
        return this.entityManagerFactory.createEntityManager();
    }

    public PersistentClass getPersistentClass(String entityName) {
        return ((MetamodelImplementor) this.entityManagerFactory.getMetamodel()).getTypeConfiguration()
                .getMetadataBuildingContext().getMetadataCollector().getEntityBinding(entityName);
    }

    /**
     * 创建对应的原生SQL方式的访问模板
     *
     * @return 原生SQL方式的访问模板
     */
    public DataAccessTemplate createNative() {
        JpaAccessTemplate template = new JpaAccessTemplate(this.schema, this.entityManagerFactory);
        template.nativeMode = true;
        return template;
    }

    public void flush() {
        getCurrentEntityManager().flush();
    }

    public void refresh(Entity entity) {
        getCurrentEntityManager().refresh(entity);
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
            return getCurrentEntityManager().createNativeQuery(ql.toString());
        } else {
            return getCurrentEntityManager().createQuery(ql.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(CharSequence ql, String paramName, Object paramValue, int pageSize, int pageNo) {
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
    public <T> List<T> listWithOneMore(CharSequence ql, String paramName, Object paramValue, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamToQuery(query, paramName, paramValue);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listWithOneMore(CharSequence ql, Map<String, ?> params, int pageSize, int pageNo) {
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
