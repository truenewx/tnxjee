package org.truenewx.tnxjee.repo.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.Repository;
import org.truenewx.tnxjee.core.spring.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;

/**
 * Repo工厂实现
 *
 * @author jianglei
 */
@org.springframework.stereotype.Repository
public class RepoFactoryImpl
        implements RepoFactory, ApplicationContextAware, ContextInitializedBean {

    private ApplicationContext context;
    private Map<Class<?>, Repo<?>> repoMapping = new HashMap<>();
    private Map<Class<?>, Repository<?, ?>> repositoryMapping = new HashMap<>();

    @Override // 确保容器启动过程中有context
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void afterInitialized(ApplicationContext context) throws Exception {
        this.context = context;

        Map<String, Repo> beans = this.context.getBeansOfType(Repo.class);
        for (Repo<?> repo : beans.values()) {
            this.repoMapping.put(repo.getEntityClass(), repo);
        }

        Map<String, Repository> repositories = this.context.getBeansOfType(Repository.class);
        for (Repository<?, ?> repository : repositories.values()) {
            Class<?> entityClass = ClassUtil.getActualGenericType(repository.getClass(), 0);
            this.repositoryMapping.put(entityClass, repository);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity, R extends Repo<T>> R getRepoByEntityClass(Class<T> entityClass) {
        return (R) this.repoMapping.get(entityClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Repo<?>> R getRepoByRepoClass(Class<R> repoClass) {
        R repo = (R) this.repoMapping.get(repoClass);
        if (repo == null) {
            try {
                repo = this.context.getBean(repoClass);
                this.repoMapping.put(repoClass, repo);
            } catch (BeansException e) {
            }
        }
        return repo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity, R extends Repository<T, ?>> R getRepositoryByEntityClass(
            Class<T> entityClass) {
        return (R) this.repositoryMapping.get(entityClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Repository<?, ?>> R getRepositoryByRepositoryClass(Class<R> repositoryClass) {
        R repository = (R) this.repositoryMapping.get(repositoryClass);
        if (repository == null) {
            try {
                repository = this.context.getBean(repositoryClass);
                this.repositoryMapping.put(repositoryClass, repository);
            } catch (BeansException e) {
            }
        }
        return repository;
    }

}
