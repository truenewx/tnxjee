package org.truenewx.tnxjee.repo.spring.config;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder.Builder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * JPA事务配置支持
 *
 * @author jianglei
 */
public abstract class JpaTransactionConfigSupport implements ApplicationContextAware {

    @Autowired
    private JpaProperties jpaProperties;
    @Autowired
    private HibernateProperties hibernateProperties;

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    private Map<String, Object> getVendorProperties() {
        return this.hibernateProperties.determineHibernateProperties(
                this.jpaProperties.getProperties(), new HibernateSettings());
    }

    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        Builder b = builder.dataSource(getDataSource()).properties(getVendorProperties())
                .persistenceUnit(getPersistenceUnitName());
        String[] mappingResources = getMappingResources();
        if (ArrayUtils.isNotEmpty(mappingResources)) {
            b.mappingResources(mappingResources);
        }
        return b.build();
    }

    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactory(builder).getObject().createEntityManager();
    }

    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactory(builder).getObject());
    }

    protected DataSource getDataSource() {
        String beanName = getDataSourceBeanName();
        if (StringUtils.isNotBlank(beanName)) {
            return this.context.getBean(beanName, DataSource.class);
        } else {
            return this.context.getBean(DataSource.class);
        }
    }

    protected String getDataSourceBeanName() {
        return null;
    }

    protected String getPersistenceUnitName() {
        return "persistenceUnit";
    }

    protected boolean isJta() {
        return false;
    }

    protected String[] getMappingResources() {
        return null;
    }

}
