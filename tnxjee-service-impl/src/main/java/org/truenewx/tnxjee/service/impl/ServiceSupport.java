package org.truenewx.tnxjee.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.spring.beans.factory.TransactionalBeanFactory;
import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.repo.Repo;
import org.truenewx.tnxjee.repo.support.RepoFactory;
import org.truenewx.tnxjee.service.api.Service;
import org.truenewx.tnxjee.service.api.ServiceFactory;

/**
 * 服务支持，具有快速获取其它服务和DAO的能力
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ServiceSupport {

    private TransactionalBeanFactory beanFactory;
    private RepoFactory repoFactory;
    private ServiceFactory serviceFactory;

    public void setBeanFactory(TransactionalBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Autowired
    public void setRepoFactory(RepoFactory repoFactory) {
        this.repoFactory = repoFactory;
    }

    @Autowired
    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    /**
     * 获取指定类型的bean。无缓存，请谨慎使用
     *
     * @param beanClass 类型
     * @return bean
     */
    protected <B> B getBean(Class<B> beanClass) {
        B bean = this.beanFactory.getBean(beanClass, false);
        if (bean == null) {
            bean = this.beanFactory.getBean(beanClass, true);
        }
        return bean;
    }

    protected <R extends Repo<T>, T extends Entity> R getRepo(Class<T> entityClass) {
        return this.repoFactory.getRepoByEntityClass(entityClass);
    }

    protected <S extends Service> S getTransactionalService(Class<S> serviceClass) {
        return this.serviceFactory.getService(serviceClass, true);
    }

    protected <S extends Service> S getService(Class<S> serviceClass) {
        S service = this.serviceFactory.getService(serviceClass, false); // 默认取非事务性服务
        if (service == null) { // 如果没有非事务性服务，则取事务性服务
            service = getTransactionalService(serviceClass);
        }
        return service;
    }

}
