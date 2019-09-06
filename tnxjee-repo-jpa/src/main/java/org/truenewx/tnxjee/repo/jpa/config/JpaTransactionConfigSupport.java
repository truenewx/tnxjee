package org.truenewx.tnxjee.repo.jpa.config;

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
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.repo.jpa.support.JpaSchemaTemplate;
import org.truenewx.tnxjee.repo.support.SchemaTemplate;

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
                .persistenceUnit(getPersistenceUnit());
        String[] entityPackages = getEntityPackages();
        if (ArrayUtils.isNotEmpty(entityPackages)) {
            b.packages(entityPackages);
        }
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

    public SchemaTemplate schemaTemplate(EntityManagerFactoryBuilder builder) {
        EntityManager entityManager = entityManager(builder);
        String schema = getSchema();
        if (schema == null) {
            return new JpaSchemaTemplate(entityManager);
        } else {
            return new JpaSchemaTemplate(schema, entityManager);
        }
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

    protected String getPersistenceUnit() {
        String persistenceUnit = "persistenceUnit";
        String schema = getSchema();
        if (StringUtils.isNotBlank(schema)) {
            persistenceUnit += StringUtil.firstToUpperCase(schema);
        }
        return persistenceUnit;
    }

    protected String getSchema() {
        return null;
    }

    /**
     * 当同一个包下的所有实体均映射到同一个数据源时，可覆写本方法提供包名，并在实体类中用注解配置映射
     *
     * @return 实体类所在包名集合
     */
    protected String[] getEntityPackages() {
        return null;
    }

    /**
     * 当同一个包下的实体映射到不同数据源时，需覆写本方法提供配置文件路径，并在配置文件中配置映射
     *
     * @return 实体映射配置文件的classpath路径
     */
    protected String[] getMappingResources() {
        return null;
    }

}
