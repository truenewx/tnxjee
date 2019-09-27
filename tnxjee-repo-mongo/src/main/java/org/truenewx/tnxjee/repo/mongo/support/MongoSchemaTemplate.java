package org.truenewx.tnxjee.repo.mongo.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.QuerySort;
import org.truenewx.tnxjee.repo.support.SchemaTemplate;
import org.truenewx.tnxjee.repo.util.RepoUtil;

/**
 * MongoDB的数据库模式访问模板
 *
 * @author jianglei
 */
public class MongoSchemaTemplate implements SchemaTemplate {

    private String schema = RepoUtil.DEFAULT_SCHEMA_NAME;
    private MongoOperations mongoOperations;

    public MongoSchemaTemplate(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public MongoSchemaTemplate(String schema, MongoOperations mongoOperations) {
        this.schema = schema;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public Iterable<Class<?>> getEntityClasses() {
        List<Class<?>> entityClasses = new ArrayList<>();
        this.mongoOperations.getConverter().getMappingContext().getPersistentEntities()
                .forEach(entity -> {
                    entityClasses.add(entity.getType());
                });
        return entityClasses;
    }

    public MongoOperations getMongoOperations() {
        return this.mongoOperations;
    }

    public void applyPagingToQuery(Query query, int pageSize, int pageNo, boolean oneMore) {
        if (pageSize > 0) { // 用页大小判断是否分页查询
            if (pageNo <= 0) { // 页码最小为1
                pageNo = 1;
            }
            query.skip(pageSize * (pageNo - 1));
            query.limit(oneMore ? (pageSize + 1) : pageSize);
        }
    }

    public <T extends Entity> List<T> list(Class<T> entityClass, Query query) {
        return this.mongoOperations.find(query, entityClass);
    }

    public <T extends Entity> List<T> list(Class<T> entityClass, Query query, int pageSize,
            int pageNo) {
        applyPagingToQuery(query, pageSize, pageNo, false);
        return list(entityClass, query);
    }

    public <T extends Entity> List<T> list(Class<T> entityClass, Query query, int pageSize,
            int pageNo, QuerySort sort) {
        applyPagingToQuery(query, pageSize, pageNo, false);
        query.with(RepoUtil.toSort(sort));
        return list(entityClass, query);
    }

    public <T extends Entity> List<T> list(Class<T> entityClass, Query query, Paging paging) {
        query.with(RepoUtil.toPageable(paging));
        return list(entityClass, query);
    }

    public <T extends Entity> T first(Class<T> entityClass, Query query) {
        List<T> list = list(entityClass, query, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public long count(Class<?> entityClass, Query query) {
        return this.mongoOperations.count(query, entityClass);
    }

    public <T extends Entity> List<T> listWithOneMore(Class<T> entityClass, Query query,
            int pageSize, int pageNo) {
        applyPagingToQuery(query, pageSize, pageNo, true);
        return list(entityClass, query);
    }

    public <T extends Entity> List<T> listWithOneMore(Class<T> entityClass, Query query,
            int pageSize, int pageNo, QuerySort sort) {
        query.with(RepoUtil.toSort(sort));
        return listWithOneMore(entityClass, query, pageSize, pageNo);
    }

    public <T extends Entity> List<T> listWithOneMore(Class<T> entityClass, Query query,
            Paging paging) {
        return listWithOneMore(entityClass, query, paging.getPageSize(), paging.getPageNo(),
                paging.getSort());
    }

    public long update(Class<?> entityClass, Query query, String propertyName,
            Object propertyValue) {
        Update update = Update.update(propertyName, propertyValue);
        return this.mongoOperations.upsert(query, update, entityClass).getModifiedCount();
    }

    public long update(Class<?> entityClass, Query query, Map<String, Object> values) {
        Update update = new Update();
        values.forEach((key, value) -> {
            update.set(key, value);
        });
        return this.mongoOperations.upsert(query, update, entityClass).getModifiedCount();
    }

    public long delete(Class<?> entityClass, Query query) {
        return this.mongoOperations.remove(query, entityClass).getDeletedCount();
    }

}
