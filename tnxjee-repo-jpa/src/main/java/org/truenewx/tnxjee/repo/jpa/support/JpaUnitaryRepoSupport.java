package org.truenewx.tnxjee.repo.jpa.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.definition.UnitaryEntity;
import org.truenewx.tnxjee.repo.UnitaryEntityNumberIncreasable;
import org.truenewx.tnxjee.repo.UnitaryEntityRepo;

/**
 * 具有单一标识属性的实体JPA数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class JpaUnitaryRepoSupport<T extends UnitaryEntity<K>, K extends Serializable>
        extends JpaRepoSupport<T>
        implements UnitaryEntityRepo<T, K>, UnitaryEntityNumberIncreasable<T, K> {

    protected T find(K key) {
        return getSchemaTemplate().find(getEntityClass(), key);
    }

    @Override
    public T increaseNumber(K key, String propertyName, Number step, Number limit) {
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            String entityName = getEntityName();
            String keyPropertyName = getKeyPropertyName();
            StringBuffer ql = new StringBuffer("update ").append(entityName).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append("+:step where ").append(keyPropertyName).append("=:key");
            Map<String, Object> params = new HashMap<>();
            params.put("key", key);
            params.put("step", step);

            if (doIncreaseNumber(ql, params, propertyName, stepValue)) {
                // 正确更新字段后需刷新实体
                T entity = find(key);
                try {
                    refresh(entity);
                } catch (Exception e) { // 忽略刷新失败
                    LogUtil.error(getClass(), e);
                }
                return entity;
            }
        }
        return null;
    }

    protected abstract String getKeyPropertyName();

}
