package org.truenewx.tnxjee.repo.jpa.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
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
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.repo.jpa.support.JpaAccessTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JPA数据层配置
 *
 * @author jianglei
 */
@Configuration
@EnableConfigurationProperties(HibernateProperties.class)
public class JpaDataConfiguration extends JpaBaseConfiguration {

    @Autowired
    private HibernateProperties hibernateProperties;
    private ApplicationContext context;

    /**
     * 在多数据源场景下，可创建子类，构造函数中指定DataSourceProperties和DataSource的beanName
     */
    public JpaDataConfiguration(ApplicationContext context,
            DataSourceProperties dataSourceProperties,
            DataSource dataSource,
            JpaProperties properties,
            ObjectProvider<JtaTransactionManager> jtaTransactionManager,
            ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManager, transactionManagerCustomizers);
        this.context = context;
        // 当前配置会在数据源对象构建完之后，在数据源初始化之前加载，接下来spring-data-jpa框架会检查数据库表结构，
        // 此时如果数据库表结构未初始化，则会报错退出，导致后续的DataSourceInitializerInvoker无法执行，
        // 悲剧的是，通过@AutoConfigureAfter指定加载顺序，仍然无法在DataSourceInitializerInvoker之后执行，
        // 所以需要在此时提前执行数据库初始化脚本，以确保数据库表结构检查通过
        initDataSource(dataSourceProperties);
    }

    private void initDataSource(DataSourceProperties properties) {
        List<String> locations = new ArrayList<>();
        List<String> schema = properties.getSchema();
        if (schema != null) {
            locations.addAll(schema);
            schema.clear(); // 清除以避免框架再次执行
        }
        List<String> data = properties.getData();
        if (data != null) {
            locations.addAll(data);
            data.clear(); // 清除以避免框架再次执行
        }
        if (locations.size() > 0) {
            DataSourceInitializer initializer = new DataSourceInitializer();
            initializer.setDataSource(getDataSource());
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            locations.forEach(location -> {
                populator.addScript(this.context.getResource(location));
            });
            initializer.setDatabasePopulator(populator);
            initializer.afterPropertiesSet();
        }
    }

    protected String getSchema() {
        return null;
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
            String location = getMappingLocation();
            // 确保目录以/开头和结尾
            if (!location.startsWith(Strings.SLASH)) {
                location = Strings.SLASH + location;
            }
            if (!location.endsWith(Strings.SLASH)) {
                location += Strings.SLASH;
            }
            Resource[] resources = this.context.getResources("classpath*:" + location + "*.xml");
            for (Resource resource : resources) {
                String path = resource.getURI().toString();
                int index = path.lastIndexOf(location);
                mappingResources.add(path.substring(index + 1));
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

    /**
     * 获取实体映射配置文件所在目录，相对于classpath
     *
     * @return 实体映射配置文件所在目录
     */
    protected String getMappingLocation() {
        return "/META-INF/jpa/";
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
