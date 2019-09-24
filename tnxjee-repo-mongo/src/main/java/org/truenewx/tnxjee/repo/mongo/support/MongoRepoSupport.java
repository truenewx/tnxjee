package org.truenewx.tnxjee.repo.mongo.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.Queried;
import org.truenewx.tnxjee.model.query.QuerySort;
import org.truenewx.tnxjee.model.query.Querying;
import org.truenewx.tnxjee.repo.mongo.util.MongoQueryUtil;
import org.truenewx.tnxjee.repo.support.RepoSupport;

/**
 * MongoDB数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class MongoRepoSupport<T extends Entity> extends RepoSupport<T> {

    @Override
    protected MongoSchemaTemplate getSchemaTemplate() {
        return (MongoSchemaTemplate) super.getSchemaTemplate();
    }

    private Queried<T> query(List<Criteria> criterias, int pageSize, int pageNo, QuerySort sort,
            boolean totalable, boolean listable) {
        Query query = MongoQueryUtil.buildQuery(criterias);
        Long total = null;
        // 需分页查询且需要获取总数时，才获取总数
        if ((pageSize > 0 || !listable) && totalable) {
            total = getSchemaTemplate().count(getEntityClass(), query);
        }
        List<T> records;
        if ((total != null && total == 0) || !listable) {
            records = new ArrayList<>();
        } else {
            records = getSchemaTemplate().list(getEntityClass(), query, pageSize, pageNo, sort);
        }
        return Queried.of(records, pageSize, pageNo, total);
    }

    protected Queried<T> query(List<Criteria> criterias, Querying querying) {
        Paging paging = querying.getPaging();
        return query(criterias, paging.getPageSize(), paging.getPageNo(), paging.getSort(),
                querying.isTotalable(), querying.isListable());
    }

    protected Queried<T> query(List<Criteria> criterias, Paging paging) {
        return query(criterias, paging.getPageSize(), paging.getPageNo(), paging.getSort());
    }

    protected Queried<T> query(List<Criteria> criterias, int pageSize, int pageNo, QuerySort sort) {
        return query(criterias, pageSize, pageNo, sort, true, true);
    }

}
