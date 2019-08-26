package org.truenewx.tnxjee.repo.support;

import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;

/**
 * Repo工厂
 *
 * @author jianglei
 */
public interface RepoFactory {

    <R extends Repo<?>> R getRepoByRepoClass(Class<R> repoClass);

    <T extends Entity, R extends Repo<T>> R getRepoByEntityClass(Class<T> entityClass);

}
