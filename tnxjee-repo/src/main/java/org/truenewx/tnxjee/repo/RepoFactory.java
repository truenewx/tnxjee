package org.truenewx.tnxjee.repo;

import org.truenewx.tnxjee.model.definition.Entity;

/**
 * Repo工厂
 *
 * @author jianglei
 */
public interface RepoFactory {

    <T extends Entity, R extends Repo<T>> R getRepoByEntityClass(Class<T> entityClass);

    <R extends Repo<?>> R getRepoByRepoClass(Class<R> repoClass);

}
