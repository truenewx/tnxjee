package org.truenewx.tnxjee.repo.mongo.support;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.QueryModel;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.model.query.QuerySort;
import org.truenewx.tnxjee.repo.mongo.util.MongoQueryUtil;
import org.truenewx.tnxjee.repo.support.RepoSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class MongoRepoSupport<T extends Entity> extends RepoSupport<T> {

    @Override
    public String getEntityName() {
        return getAccessTemplate().getMongoOperations().getCollectionName(getEntityClass());
    }

    @Override
    protected MongoAccessTemplate getAccessTemplate() {
        return (MongoAccessTemplate) super.getAccessTemplate();
    }

    private QueryResult<T> query(List<Criteria> criterias, int pageSize, int pageNo, QuerySort sort, boolean totalable,
            boolean listable) {
        Query query = MongoQueryUtil.buildQuery(criterias);
        Long total = null;
        // 需分页查询且需要获取总数时，才获取总数
        if ((pageSize > 0 || !listable) && totalable) {
            total = getAccessTemplate().count(getEntityClass(), query);
        }
        List<T> records;
        if ((total != null && total == 0) || !listable) {
            records = new ArrayList<>();
        } else {
            records = getAccessTemplate().list(getEntityClass(), query, pageSize, pageNo, sort);
        }
        return QueryResult.of(records, pageSize, pageNo, total);
    }

    protected QueryResult<T> query(List<Criteria> criterias, QueryModel queryModel) {
        Paging paging = queryModel.getPaging();
        return query(criterias, paging.getPageSize(), paging.getPageNo(), paging.getSort(), queryModel.isTotalable(),
                queryModel.isListable());
    }

    protected QueryResult<T> query(List<Criteria> criterias, Paging paging) {
        return query(criterias, paging.getPageSize(), paging.getPageNo(), paging.getSort());
    }

    protected QueryResult<T> query(List<Criteria> criterias, int pageSize, int pageNo, QuerySort sort) {
        return query(criterias, pageSize, pageNo, sort, true, true);
    }

}
