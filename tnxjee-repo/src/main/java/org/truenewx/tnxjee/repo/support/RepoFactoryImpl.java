package org.truenewx.tnxjee.repo.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.core.spring.beans.ContextInitializedBean;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;

/**
 * Repo工厂实现
 *
 * @author jianglei
 */
@Repository
public class RepoFactoryImpl
        implements RepoFactory, ApplicationContextAware, ContextInitializedBean {

    private ApplicationContext context;
    private Map<Class<?>, Repo<?>> repoMapping = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        this.context = context;
        @SuppressWarnings("rawtypes")
        Map<String, Repo> beans = this.context.getBeansOfType(Repo.class);
        for (Repo<?> dao : beans.values()) {
            this.repoMapping.put(dao.getEntityClass(), dao);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Repo<?>> R getRepoByRepoClass(Class<R> daoClass) {
        R dao = (R) this.repoMapping.get(daoClass);
        if (dao == null) {
            try {
                dao = this.context.getBean(daoClass);
                this.repoMapping.put(daoClass, dao);
            } catch (BeansException e) {
            }
        }
        return dao;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity, R extends Repo<T>> R getRepoByEntityClass(Class<T> entityClass) {
        return (R) this.repoMapping.get(entityClass);
    }

}
