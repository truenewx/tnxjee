package org.truenewx.tnxjee.repo.jpa.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.core.relation.Relation;
import org.truenewx.tnxjee.repo.RelationNumberIncreasable;
import org.truenewx.tnxjee.repo.RelationRepo;

/**
 * 关系JPA数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class JpaRelationRepoSupport<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends JpaRepoSupport<T>
        implements RelationRepo<T, L, R>, RelationNumberIncreasable<T, L, R> {

    /**
     * 获取标识属性对，left-左标识属性名，right-右标识属性名
     *
     * @return 标识属性对，不能返回null
     */
    protected abstract Binate<String, String> getIdProperty();

    @Override
    public Optional<T> findById(L leftId, R rightId) {
        return Optional.ofNullable(find(leftId, rightId));
    }

    protected T find(L leftId, R rightId) {
        if (leftId != null && rightId != null) {
            Map<String, Object> params = new HashMap<>();
            StringBuffer ql = buildQlById(params, leftId, rightId);
            return getSchemaTemplate().first(ql, params);
        }
        return null;
    }

    private StringBuffer buildQlById(Map<String, Object> params, L leftId, R rightId) {
        Binate<String, String> idProperty = getIdProperty();
        StringBuffer ql = new StringBuffer("from ").append(getEntityName()).append(" r where r.")
                .append(idProperty.getLeft()).append("=:leftId and r.")
                .append(idProperty.getRight()).append("=:rightId");
        params.put("leftId", leftId);
        params.put("rightId", rightId);
        return ql;
    }

    @Override
    public boolean exists(L leftId, R rightId) {
        if (leftId != null && rightId != null) {
            Map<String, Object> params = new HashMap<>();
            StringBuffer ql = buildQlById(params, leftId, rightId);
            ql.insert(0, "select count(*) ");
            return getSchemaTemplate().count(ql, params) > 0;
        }
        return false;
    }

    @Override
    public T increaseNumber(L leftId, R rightId, String propertyName, Number step, Number limit) {
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            Binate<String, String> idProperty = getIdProperty();
            StringBuffer hql = new StringBuffer("update ").append(getEntityName()).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append("+:step where ").append(idProperty.getLeft()).append(Strings.EQUAL)
                    .append("=:leftId and ").append(idProperty.getRight()).append("=:rightId");
            Map<String, Object> params = new HashMap<>();
            params.put("leftId", leftId);
            params.put("rightId", rightId);
            params.put("step", step);

            if (doIncreaseNumber(hql, params, propertyName, stepValue)) {
                // 更新字段后需刷新实体
                T unity = find(leftId, rightId);
                try {
                    refresh(unity);
                } catch (Exception e) { // 忽略刷新失败
                    LogUtil.error(getClass(), e);
                }
                return unity;
            }
        }
        return null;
    }
}
