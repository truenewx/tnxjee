package org.truenewx.tnxjee.repo.jpa.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

/**
 * JPA数据源配置属性集
 *
 * @author jianglei
 */
public class JpaDataSourceProperties extends DataSourceProperties {

    private Integer maxIdleTime;
    private Integer maxLifetime;
    private Integer maxPoolSize;
    private Integer minPoolSize;
    private Integer poolSize;
    private Integer reapTimeout;
    private String testQuery;

    public Integer getMaxIdleTime() {
        return this.maxIdleTime;
    }

    public void setMaxIdleTime(Integer maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public Integer getMaxLifetime() {
        return this.maxLifetime;
    }

    public void setMaxLifetime(Integer maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public Integer getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getMinPoolSize() {
        return this.minPoolSize;
    }

    public void setMinPoolSize(Integer minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public Integer getPoolSize() {
        return this.poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public Integer getReapTimeout() {
        return this.reapTimeout;
    }

    public void setReapTimeout(Integer reapTimeout) {
        this.reapTimeout = reapTimeout;
    }

    public String getTestQuery() {
        return this.testQuery;
    }

    public void setTestQuery(String testQuery) {
        this.testQuery = testQuery;
    }

}
