package org.truenewx.tnxjee.repo.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.Queried;
import org.truenewx.tnxjee.model.query.QuerySort;
import org.truenewx.tnxjee.model.query.Querying;
import org.truenewx.tnxjee.repo.Repo;
import org.truenewx.tnxjee.repo.util.OqlUtil;

/**
 * 数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class RepoSupport<T extends Entity> implements Repo<T> {
    @Autowired
    private RepoFactory repoFactory;

    /**
     * 获取实体类型<br/>
     * 默认实现通过反射机制获取，子类可覆写直接返回具体实体的类型以优化性能
     *
     * @return 实体类型
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getEntityClass() {
        return (Class<T>) ClassUtil.getActualGenericType(getClass(), 0);
    }

    protected final <K, R extends Repository<T, K>> R getRepository() {
        return this.repoFactory.getRepositoryByEntityClass(getEntityClass());
    }

    protected abstract DataAccessTemplate getDataAccessTemplate(String entityName);

    protected Class<?> getPropertyClass(String propertyName) {
        Field field = ClassUtil.findField(getEntityClass(), propertyName);
        return field == null ? null : field.getType();
    }

    protected T first(String entityName) {
        StringBuffer hql = new StringBuffer("from ").append(entityName);
        return getDataAccessTemplate(entityName).first(hql, (Map<String, Object>) null);
    }

    private Queried<T> query(String entityName, CharSequence ql, Map<String, Object> params,
            int pageSize, int pageNo, QuerySort sort, boolean totalable, boolean listable) {
        Long total = null;
        if ((pageSize > 0 || !listable) && totalable) { // 需分页查询且需要获取总数时，才获取总数
            total = getDataAccessTemplate(entityName).count("select count(*) " + ql, params);
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
            dataList = getDataAccessTemplate(entityName).list(ql, params, pageSize, pageNo);
            if (pageSize <= 0) { // 非分页查询，总数为结果记录条数
                total = (long) dataList.size();
            }
        }
        return Queried.of(dataList, pageSize, pageNo, total);
    }

    protected Queried<T> query(String entityName, CharSequence ql, Map<String, Object> params,
            Querying querying) {
        Paging paging = querying.getPaging();
        return query(entityName, ql, params, paging.getPageSize(), paging.getPageNo(),
                paging.getSort(), querying.isTotalable(), querying.isListable());
    }

    protected Queried<T> query(String entityName, CharSequence ql, Map<String, Object> params,
            int pageSize, int pageNo, QuerySort sort) {
        return query(entityName, ql, params, pageSize, pageNo, sort, true, true);
    }

    protected Queried<T> query(String entityName, CharSequence ql, Map<String, Object> params,
            Paging paging) {
        return query(entityName, ql, params, paging.getPageSize(), paging.getPageNo(),
                paging.getSort());
    }

    protected long countAll(String entityName) {
        String hql = "select count(*) from " + entityName;
        return getDataAccessTemplate(entityName).count(hql, (Map<String, ?>) null);
    }

}
