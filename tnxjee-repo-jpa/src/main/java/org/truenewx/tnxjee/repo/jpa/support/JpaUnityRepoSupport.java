package org.truenewx.tnxjee.repo.jpa.support;

import java.io.Serializable;
import java.util.Optional;

import org.truenewx.tnxjee.model.definition.unity.Unity;
import org.truenewx.tnxjee.repo.UnityRepo;

/**
 * 单体JPA数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class JpaUnityRepoSupport<T extends Unity<K>, K extends Serializable>
        extends JpaUnitaryRepoSupport<T, K> implements UnityRepo<T, K> {

    @Override
    protected String getKeyPropertyName() {
        return "id";
    }

    @Override
    public Optional<T> findById(K id) {
        return Optional.ofNullable(find(id));
    }

}