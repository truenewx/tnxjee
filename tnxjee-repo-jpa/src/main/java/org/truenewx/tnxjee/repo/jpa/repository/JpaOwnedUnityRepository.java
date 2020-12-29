package org.truenewx.tnxjee.repo.jpa.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;
import org.truenewx.tnxjee.repo.OwnedUnityRepo;

public interface JpaOwnedUnityRepository<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends JpaRepository<T, K>, OwnedUnityRepo<T, K, O> {
}
