package org.truenewx.tnxjee.repo.jdbc.datasource.lookup;

import javax.sql.DataSource;

/**
 * 数据源检索
 *
 * @author jianglei
 */
public interface DataSourceLookup {

    /**
     * 根据实体名称获取对应的数据源
     *
     * @param entityName 实体名称
     * @return 数据源
     */
    DataSource getDataSource(String entityName);

}
