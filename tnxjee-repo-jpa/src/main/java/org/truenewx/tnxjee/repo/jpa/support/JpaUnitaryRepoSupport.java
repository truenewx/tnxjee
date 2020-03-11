package org.truenewx.tnxjee.repo.jpa.support;

import org.springframework.data.repository.CrudRepository;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.entity.UnitaryEntity;
import org.truenewx.tnxjee.repo.UnitaryEntityRepo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 具有单一标识属性的实体JPA数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class JpaUnitaryRepoSupport<T extends UnitaryEntity<K>, K extends Serializable>
        extends JpaRepoSupport<T> implements UnitaryEntityRepo<T, K> {

    protected T find(K key) {
        CrudRepository<T, K> repository = getRepository();
        return repository.findById(key).orElse(null);
    }

    @Override
    public <N extends Number> T increaseNumber(K key, String propertyName, N step, N limit) {
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

            if (doIncreaseNumber(ql, params, propertyName, stepValue > 0, limit)) {
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
