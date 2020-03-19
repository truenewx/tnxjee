package org.truenewx.tnxjee.repo.mongo.support;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.repo.UnityRepo;

/**
 * MongoDB单体数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class MongoUnityRepoSupport<T extends Unity<K>, K extends Serializable> extends MongoRepoSupport<T>
        implements UnityRepo<T, K> {

    protected final T find(K id) {
        CrudRepository<T, K> repository = getRepository();
        return repository.findById(id).orElse(null);
    }

    @Override
    public <N extends Number> T increaseNumber(K id, String propertyName, N step, N limit) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
