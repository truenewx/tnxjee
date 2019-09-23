package org.truenewx.tnxjee.repo.mongo.support;

import java.io.Serializable;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.truenewx.tnxjee.model.unity.OwnedUnity;
import org.truenewx.tnxjee.repo.OwnedUnityRepo;

/**
 * MongoDB从属单体的数据访问仓库
 *
 * @author jianglei
 */
public abstract class MongoOwnedUnityRepoSupport<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends MongoUnityRepoSupport<T, K> implements OwnedUnityRepo<T, K, O> {

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
        Query query = new Query(Criteria.where(ownerProperty).is(owner));
        return getSchemaTemplate().count(getEntityClass(), query);
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
        Query query = new Query(Criteria.where(ownerProperty).is(owner).and("id").is(id));
        return getSchemaTemplate().first(getEntityClass(), query);
    }

}
