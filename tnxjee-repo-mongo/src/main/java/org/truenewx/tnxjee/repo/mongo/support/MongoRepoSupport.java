package org.truenewx.tnxjee.repo.mongo.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.Queried;
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

    protected Queried<T> query(List<Criteria> criterias, Querying querying) {
        Query query = MongoQueryUtil.buildQuery(criterias);
        long total = getSchemaTemplate().count(getEntityClass(), query);
        Paging paging = querying.getPaging();
        List<T> records;
        if (total == 0) {
            records = new ArrayList<>();
        } else {
            records = getSchemaTemplate().list(getEntityClass(), query, paging);
        }
        return Queried.of(records, paging.getPageSize(), paging.getPageNo(), total);
    }

}
