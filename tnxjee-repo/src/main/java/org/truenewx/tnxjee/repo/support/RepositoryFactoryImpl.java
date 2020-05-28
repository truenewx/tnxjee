package org.truenewx.tnxjee.repo.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.repo.Repo;

/**
 * Repository工厂实现
 *
 * @author jianglei
 */
@Component
public class RepositoryFactoryImpl implements RepositoryFactory, ContextInitializedBean {

    private Map<Class<?>, Repository<?, ?>> repositoryMapping = new HashMap<>();
    private Map<Class<?>, Repo<?>> repoMapping = new HashMap<>();

    @Override
    @SuppressWarnings("rawtypes")
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, Repository> repositories = context.getBeansOfType(Repository.class);
        for (Repository<?, ?> repository : repositories.values()) {
            Class<?> entityClass = getEntityClass(repository);
            if (entityClass != null) {
                this.repositoryMapping.put(entityClass, repository);
            }
        }

        Map<String, Repo> beans = context.getBeansOfType(Repo.class);
        for (Repo<?> repo : beans.values()) {
            Class<?> entityClass = getEntityClass(repo);
            if (entityClass != null) {
                this.repoMapping.put(entityClass, repo);
            }
        }
    }

    private Class<?> getEntityClass(Repository<?, ?> repository) {
        Class<?>[] interfaces = repository.getClass().getInterfaces();
        for (Class<?> clazz : interfaces) {
            if (clazz != Repository.class && Repository.class.isAssignableFrom(clazz)) {
                return ClassUtil.getActualGenericType(clazz, Repository.class, 0);
            }
        }
        return null;
    }

    private Class<?> getEntityClass(Repo<?> repo) {
        Class<?> entityClass = getEntityClassOfRepoClass(repo.getClass());
        if (entityClass == null && repo instanceof Advised) {
            entityClass = getEntityClassOfRepoClass(((Advised) repo).getTargetClass());
        }
        return entityClass;
    }

    private Class<?> getEntityClassOfRepoClass(Class<?> repoClass) {
        Class<?>[] interfaces = repoClass.getInterfaces();
        for (Class<?> clazz : interfaces) {
            if (clazz != Repo.class && Repo.class.isAssignableFrom(clazz)) {
                return ClassUtil.getActualGenericType(clazz, Repo.class, 0);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends CrudRepository<T, K>, T extends Entity, K> R getRepository(Class<T> entityClass) {
        return (R) this.repositoryMapping.get(entityClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Repo<T>, T extends Entity> R getRepo(Class<T> entityClass) {
        return (R) this.repoMapping.get(entityClass);
    }

}
