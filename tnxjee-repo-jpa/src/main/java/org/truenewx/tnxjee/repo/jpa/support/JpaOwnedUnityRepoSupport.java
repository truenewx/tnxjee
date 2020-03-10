package org.truenewx.tnxjee.repo.jpa.support;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;
import org.truenewx.tnxjee.repo.OwnedUnityRepo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 从属单体的数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class JpaOwnedUnityRepoSupport<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends JpaUnityRepoSupport<T, K>
        implements OwnedUnityRepo<T, K, O> {

    /**
     * 获取所属者属性名<br/>
     * 默认返回null，此时通过标识获取单体后判断所属者是否匹配，可由子类覆写返回非null的值，从而通过所属字段限制单体查询<br/>
     * 建议：当所属者为引用对象下的属性时 ，子类覆写提供非null的返回值，否则不覆写
     *
     * @return 所属者属性
     */
    protected String getOwnerProperty() {
        return null;
    }

    @Override
    public long countByOwner(O owner) {
        String ownerProperty = getOwnerProperty();
        if (ownerProperty == null) {
            throw new UnsupportedOperationException();
        }
        StringBuffer ql = new StringBuffer("select count(*) from ").append(getEntityName())
                .append(" e where e.").append(ownerProperty).append("=:owner");
        return getAccessTemplate().count(ql.toString(), "owner", owner);
    }

    @Override
    public T findByOwnerAndId(O owner, K id) {
        if (id == null) {
            return null;
        }
        String ownerProperty = getOwnerProperty();
        if (ownerProperty == null) {
            T entity = find(id);
            if (entity != null && owner.equals(entity.getOwner())) {
                return entity;
            }
            return null;
        }
        StringBuffer ql = new StringBuffer("from ").append(getEntityName()).append(" e where e.")
                .append(ownerProperty).append("=:owner and e.id=:id");
        Map<String, Object> params = new HashMap<>();
        params.put("owner", owner);
        params.put("id", id);
        return getAccessTemplate().first(ql.toString(), params);
    }

    @Override
    public T increaseNumber(O owner, K id, String propertyName, Number step, Number limit) {
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            String entityName = getEntityName();
            StringBuffer hql = new StringBuffer("update ").append(entityName).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append("+:step where id=:id");
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("step", step);

            String ownerProperty = getOwnerProperty();
            if (owner != null && ownerProperty != null) {
                hql.append(" and ").append(ownerProperty).append("=:owner");
                params.put("owner", owner);
            }

            if (doIncreaseNumber(hql, params, propertyName, stepValue)) {
                // 更新字段后需刷新实体
                T unity = find(id);
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
