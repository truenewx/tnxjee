package org.truenewx.tnxjee.repo.hibernate.function;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.stereotype.Repository;

/**
 * MySql的表存在断言
 *
 * @author jianglei
 */
@Repository
public class MySqlTableExistsPredicate extends JdbcTableExistsPredicate {

    @Override
    public Class<? extends Dialect> getDialectClass() {
        return MySQLDialect.class;
    }

    @Override
    protected String getQuerySql() {
        return "show tables like ?";
    }

}
