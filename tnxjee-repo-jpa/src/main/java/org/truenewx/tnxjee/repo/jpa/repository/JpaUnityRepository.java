package org.truenewx.tnxjee.repo.jpa.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.repo.UnityRepo;

/**
 * 为JPA单体访问库提供的便捷接口，非必须，业务Repo接口可同时继承{@link JpaRepository}和{@link UnityRepo}达到相同的效果
 *
 * @author jianglei
 */
@NoRepositoryBean
public interface JpaUnityRepository<T extends Unity<K>, K extends Serializable>
        extends JpaRepository<T, K>, UnityRepo<T, K> {

}
