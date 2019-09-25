package org.truenewx.tnxjee.repo.jpa.jdbc.datasource.embedded;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.AbstractDriverBasedDataSource;

/**
 * H2的XA数据源工厂Bean
 *
 * @author jianglei
 */
public class H2XaDataSourceFactoryBean implements FactoryBean<XADataSource> {
    /**
     * 内嵌数据源
     */
    private DataSource dataSource;

    public H2XaDataSourceFactoryBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public XADataSource getObject() throws Exception {
        AbstractDriverBasedDataSource dataSource = (AbstractDriverBasedDataSource) this.dataSource;
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(dataSource.getUrl());
        ds.setUser(dataSource.getUsername());
        ds.setPassword(dataSource.getPassword());
        return ds;
    }

    @Override
    public Class<?> getObjectType() {
        return XADataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
