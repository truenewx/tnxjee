package org.truenewx.tnxjee.repo.jpa.config;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder.Builder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.repo.jpa.hibernate.DefaultJtaPlatform;
import org.truenewx.tnxjee.repo.jpa.jdbc.datasource.embedded.EmbeddedDataSourceFactoryBean;
import org.truenewx.tnxjee.repo.jpa.jdbc.datasource.embedded.H2XaDataSourceFactoryBean;
import org.truenewx.tnxjee.repo.jpa.support.JpaAccessTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JPA配置支持
 *
 * @author jianglei
 */
public abstract class JpaConfigSupport implements ApplicationContextAware {

    @Autowired
    private JpaProperties jpaProperties;
    @Autowired
    private HibernateProperties hibernateProperties;
    @Autowired
    private DefaultJtaPlatform jtaPlatform; // 确保已被实例化
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    protected String getSchema() {
        return null;
    }

    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    public DataSource dataSource() {
        if (isJta()) {
            return buildDataSource(xaDataSource());
        } else {
            return DataSourceBuilder.create().build();
        }
    }

    protected XADataSource xaDataSource() {
        throw new UnsupportedOperationException();
    }

    private DataSource buildDataSource(XADataSource xaDataSource) {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setXaDataSource(xaDataSource);
        dataSource.setUniqueResourceName(getDataSourceBeanName());
        return dataSource;
    }

    // 由于Service层需要用到Repo层的单元测试环境，故内嵌数据源构建方法必须显式声明，Repo层和Service层注意将单元测试相关依赖标记为test范围
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
        DataSource dataSource = factory.getObject();
        if (isJta()) {
            dataSource = buildDataSource(new H2XaDataSourceFactoryBean(dataSource).getObject());
        }
        return dataSource;
    }

    private DataSource findDataSourceBean() {
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

    private Map<String, Object> getVendorProperties() {
        Map<String, Object> properties = this.hibernateProperties
                .determineHibernateProperties(this.jpaProperties.getProperties(), new HibernateSettings());
        if (isJta()) {
            properties.put("hibernate.transaction.jta.platform", this.jtaPlatform.getClass().getName());
            properties.put("javax.persistence.transactionType", "JTA");
        }
        return properties;
    }

    protected boolean isJta() {
        return false;
    }

    protected String getPersistenceUnit() {
        String persistenceUnit = "persistenceUnit";
        String schema = getSchema();
        if (StringUtils.isNotBlank(schema)) {
            persistenceUnit += StringUtil.firstToUpperCase(schema);
        }
        return persistenceUnit;
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
        List<String> resources = scanDefaultMappingResources();
        return resources.toArray(new String[resources.size()]);
    }

    protected final List<String> scanDefaultMappingResources() {
        List<String> list = new ArrayList<>();
        try {
            Resource[] resources = this.resourcePatternResolver.getResources("classpath*:META-INF/jpa/*.xml");
            for (Resource resource : resources) {
                String path = resource.getURI().toString();
                int index = path.lastIndexOf("/META-INF/jpa/");
                list.add(path.substring(index + 1));
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
        return list;
    }

    public FactoryBean<EntityManagerFactory> entityManagerFactory(EntityManagerFactoryBuilder builder) {
        DataSource dataSource = findDataSourceBean();
        Builder b = builder.dataSource(dataSource).properties(getVendorProperties()).jta(isJta())
                .persistenceUnit(getPersistenceUnit());
        String[] entityPackages = getEntityPackages();
        if (ArrayUtils.isNotEmpty(entityPackages)) {
            b.packages(entityPackages);
        } else { // 未指定实体类包名集，则使用映射配置文件的方式
            String[] mappingResources = getMappingResources();
            if (ArrayUtils.isNotEmpty(mappingResources)) {
                b.mappingResources(mappingResources);
            }
        }
        return b.build();
    }

    /**
     * 多数据源时，需要多个{@link JpaAccessTemplate}，子类必须覆写此方法构建{@link JpaAccessTemplate}实例
     */
    public JpaAccessTemplate jpaAccessTemplate(EntityManagerFactoryBuilder builder) throws Exception {
        EntityManagerFactory entityManagerFactory = entityManagerFactory(builder).getObject();
        return jpaAccessTemplate(entityManagerFactory);
    }

    /**
     * 单数据源时，只需一个{@link JpaAccessTemplate}，子类只需覆写此方法构建{@link JpaAccessTemplate}实例
     */
    public JpaAccessTemplate jpaAccessTemplate(EntityManagerFactory entityManagerFactory) {
        String schema = getSchema();
        if (schema == null) {
            return new JpaAccessTemplate(entityManagerFactory);
        } else {
            return new JpaAccessTemplate(schema, entityManagerFactory);
        }
    }

}
