package org.truenewx.tnxjee.repo.jpa.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.Queried;
import org.truenewx.tnxjee.model.query.QuerySort;
import org.truenewx.tnxjee.model.query.Querying;
import org.truenewx.tnxjee.repo.jpa.JpaRepo;
import org.truenewx.tnxjee.repo.support.RepoSupport;
import org.truenewx.tnxjee.repo.util.OqlUtil;

/**
 * JPA的数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class JpaRepoSupport<T extends Entity> extends RepoSupport<T>
        implements JpaRepo<T> {

    @Override
    protected JpaSchemaTemplate getSchemaTemplate() {
        return (JpaSchemaTemplate) super.getSchemaTemplate();
    }

    protected String getEntityName() {
        return getEntityClass().getName();
    }

    @Override
    public T first() {
        return getSchemaTemplate().first("from " + getEntityName(), (Map<String, Object>) null);
    }

    @Override
    public void flush() {
        getSchemaTemplate().flush();
    }

    @Override
    public void refresh(T entity) {
        getSchemaTemplate().refresh(entity);
    }

    private Queried<T> query(CharSequence ql, Map<String, Object> params, int pageSize, int pageNo,
            QuerySort sort, boolean totalable, boolean listable) {
        Long total = null;
        if ((pageSize > 0 || !listable) && totalable) { // 需分页查询且需要获取总数时，才获取总数
            total = getSchemaTemplate().count("select count(*) " + ql, params);
        }

        List<T> dataList;
        // 已知总数为0或无需查询记录清单，则不查询记录清单
        if ((total != null && total == 0) || !listable) {
            dataList = new ArrayList<>();
        } else {
            String orderString = OqlUtil.buildOrderString(sort);
            if (StringUtils.isNotBlank(orderString)) {
                if (ql instanceof StringBuffer) {
                    ((StringBuffer) ql).append(orderString);
                } else {
                    ql = ql.toString() + orderString;
                }
            }
            dataList = getSchemaTemplate().list(ql, params, pageSize, pageNo);
            if (pageSize <= 0) { // 非分页查询，总数为结果记录条数
                total = (long) dataList.size();
            }
        }
        return Queried.of(dataList, pageSize, pageNo, total);
    }

    protected Queried<T> query(CharSequence ql, Map<String, Object> params, Querying querying) {
        Paging paging = querying.getPaging();
        return query(ql, params, paging.getPageSize(), paging.getPageNo(), paging.getSort(),
                querying.isTotalable(), querying.isListable());
    }

    protected Queried<T> query(CharSequence ql, Map<String, Object> params, Paging paging) {
        return query(ql, params, paging.getPageSize(), paging.getPageNo(), paging.getSort());
    }

    protected Queried<T> query(CharSequence ql, Map<String, Object> params, int pageSize,
            int pageNo, QuerySort sort) {
        return query(ql, params, pageSize, pageNo, sort, true, true);
    }

}
