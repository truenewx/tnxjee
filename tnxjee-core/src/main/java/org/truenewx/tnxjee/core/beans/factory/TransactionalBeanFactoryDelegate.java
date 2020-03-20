package org.truenewx.tnxjee.core.beans.factory;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * 事务性Bean提交处理器
 *
 * @author jianglei
 * 
 */
@Component
public class TransactionalBeanFactoryDelegate
        implements TransactionalBeanFactory, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, boolean transactional) {
        try {
            T bean = (T) this.beanFactory.getBean(name);
            return getTarget(bean, transactional);
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T getTarget(T bean, boolean transactional) throws Exception {
        if (AopUtils.isAopProxy(bean)) {
            if (transactional) { // 取到的Bean为代理且需要的就是事务性Bean，则返回该Bean
                return bean;
            } else if (bean instanceof Advised) { // 取到的Bean为代理但需要非事务性Bean，则返回该代理的代理目标
                Advised proxy = (Advised) bean;
                return (T) proxy.getTargetSource().getTarget();
            }
        } else if (!transactional) { // 取到的Bean非代理且需要的就是非事务性Bean，则返回该Bean
            return bean;
        }
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType, boolean transactional) {
        try {
            T bean = this.beanFactory.getBean(name, requiredType);
            return getTarget(bean, transactional);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType, boolean transactional) {
        try {
            T bean = this.beanFactory.getBean(requiredType);
            return getTarget(bean, transactional);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public boolean containsBean(String name, boolean transactional) {
        return getBean(name, transactional) != null;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return this.beanFactory.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return this.beanFactory.getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return this.beanFactory.getBean(requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return this.beanFactory.getBean(name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return this.beanFactory.getBean(requiredType, args);
    }

    @Override
    public boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> targetType)
            throws NoSuchBeanDefinitionException {
        return this.beanFactory.isTypeMatch(name, targetType);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch)
            throws NoSuchBeanDefinitionException {
        return this.isTypeMatch(name, typeToMatch);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getType(name);
    }

    @Override
    public String[] getAliases(String name) {
        return this.beanFactory.getAliases(name);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return this.beanFactory.getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return this.beanFactory.getBeanProvider(requiredType);
    }

}
