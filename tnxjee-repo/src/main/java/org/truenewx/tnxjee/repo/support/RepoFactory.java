package org.truenewx.tnxjee.repo.support;

import org.springframework.data.repository.Repository;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;

/**
 * Repo工厂
 *
 * @author jianglei
 */
public interface RepoFactory {

    <T extends Entity, R extends Repo<T>> R getRepoByEntityClass(Class<T> entityClass);

    <R extends Repo<?>> R getRepoByRepoClass(Class<R> repoClass);

    <R extends Repository<?, ?>> R getRepositoryByRepositoryClass(Class<R> repositoryClass);

    <T extends Entity, R extends Repository<T, ?>> R getRepositoryByEntityClass(
            Class<T> entityClass);

}
