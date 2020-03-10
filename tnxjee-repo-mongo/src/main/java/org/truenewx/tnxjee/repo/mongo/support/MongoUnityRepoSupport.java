package org.truenewx.tnxjee.repo.mongo.support;

import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.repo.UnityRepo;

import java.io.Serializable;

/**
 * MongoDB单体数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class MongoUnityRepoSupport<T extends Unity<K>, K extends Serializable>
        extends MongoUnitaryRepoSupport<T, K> implements UnityRepo<T, K> {
    @Override
    protected String getKeyPropertyName() {
        return "id";
    }
}
