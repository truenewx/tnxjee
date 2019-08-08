package org.truenewx.tnxjee.repo.hibernate.function;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.springframework.stereotype.Repository;

/**
 * H2的表存在断言
 *
 * @author jianglei
 */
@Repository
public class H2TableExistsPredicate extends JdbcTableExistsPredicate {

    @Override
    public Class<? extends Dialect> getDialectClass() {
        return H2Dialect.class;
    }

    @Override
    protected String getQuerySql() {
        return "select table_name from information_schema.tables where table_name=?";
    }

}
