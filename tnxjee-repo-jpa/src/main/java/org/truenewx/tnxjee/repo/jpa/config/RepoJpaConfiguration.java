package org.truenewx.tnxjee.repo.jpa.config;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.repo.jpa.jdbc.datasource.embedded.EmbeddedDataSourceFactoryBean;
import org.truenewx.tnxjee.repo.jpa.support.JpaAccessTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 数据层JPA配置
 *
 * @author jianglei
 */
@Configuration
@EnableConfigurationProperties(HibernateProperties.class)
public class RepoJpaConfiguration extends JpaBaseConfiguration {

    @Autowired
    private HibernateProperties hibernateProperties;
    @Autowired
    private ApplicationContext context;

    public RepoJpaConfiguration(DataSource dataSource,
            JpaProperties properties,
            ObjectProvider<JtaTransactionManager> jtaTransactionManager,
            ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManager, transactionManagerCustomizers);
    }

    protected String getSchema() {
        return null;
    }

    public DataSource embeddedDataSource(String... scriptLocations) throws Exception {
        EmbeddedDataSourceFactoryBean factory = new EmbeddedDataSourceFactoryBean();
        factory.setDatabaseType(EmbeddedDatabaseType.H2);
        String schema = getSchema();
        if (StringUtils.isNotBlank(schema)) {
            factory.setDatabaseName(schema);
        }
        if (ArrayUtils.isNotEmpty(scriptLocations)) {
            Resource[] resources = new Resource[scriptLocations.length];
            for (int i = 0; i < scriptLocations.length; i++) {
                resources[i] = new ClassPathResource(scriptLocations[i]);
            }
            factory.setScripts(resources);
        }
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        return this.hibernateProperties.determineHibernateProperties(
                getProperties().getProperties(), new HibernateSettings());
    }

    @Override
    protected String[] getPackagesToScan() {
        return new String[0];
    }

    protected void addMappingResources(List<String> mappingResources) {
        try {
            Resource[] resources = this.context.getResources("classpath*:META-INF/jpa/*.xml");
            for (Resource resource : resources) {
                String path = resource.getURI().toString();
                int index = path.lastIndexOf("/META-INF/jpa/");
                mappingResources.add(path.substring(index + 1));
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder factoryBuilder) {
        addMappingResources(getProperties().getMappingResources());
        return super.entityManagerFactory(factoryBuilder);
    }

    @Bean
    public JpaAccessTemplate jpaAccessTemplate(EntityManagerFactory entityManagerFactory) {
        String schema = getSchema();
        if (schema == null) {
            return new JpaAccessTemplate(entityManagerFactory);
        } else {
            return new JpaAccessTemplate(schema, entityManagerFactory);
        }
    }

}
