package org.truenewx.tnxjee.repo.support;

import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.repo.Repo;

/**
 * Repo工厂
 *
 * @author jianglei
 */
public interface RepoFactory {

    <T extends Entity, R extends Repo<T>> R getRepoByEntityClass(Class<T> entityClass);

    <R extends Repo<?>> R getRepoByRepoClass(Class<R> repoClass);

}
