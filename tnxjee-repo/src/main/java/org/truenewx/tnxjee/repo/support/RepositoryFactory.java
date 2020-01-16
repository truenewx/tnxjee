package org.truenewx.tnxjee.repo.support;

import org.springframework.data.repository.Repository;
import org.truenewx.tnxjee.model.entity.Entity;

/**
 *
 * @author jianglei
 */
public interface RepositoryFactory {

    <T extends Entity, R extends Repository<T, ?>> R getRepositoryByEntityClass(
            Class<T> entityClass);

    <T extends Entity, R extends Repository<T, ?>> void putRepositoryIfAbsent(Class<T> entityClass,
            R repository);

    <R extends Repository<?, ?>> R getRepositoryByRepositoryClass(Class<R> repositoryClass);

}
