package org.truenewx.tnxjee.repo.mongo.support;

import org.springframework.data.repository.CrudRepository;
import org.truenewx.tnxjee.model.entity.UnitaryEntity;
import org.truenewx.tnxjee.repo.UnitaryEntityRepo;

import java.io.Serializable;

/**
 * MongoDB单一标识实体数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class MongoUnitaryRepoSupport<T extends UnitaryEntity<K>, K extends Serializable>
        extends MongoRepoSupport<T> implements UnitaryEntityRepo<T, K> {

    protected T find(K key) {
        CrudRepository<T, K> repository = getRepository();
        return repository.findById(key).orElse(null);
    }

    @Override
    public <N extends Number> T increaseNumber(K key, String propertyName, N step, N limit) {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected abstract String getKeyPropertyName();

}
