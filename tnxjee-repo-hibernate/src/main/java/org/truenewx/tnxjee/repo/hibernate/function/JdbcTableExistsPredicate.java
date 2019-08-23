package org.truenewx.tnxjee.repo.hibernate.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

/**
 * 基于JDBC查询的表存在断言
 *
 * @author jianglei
 */
public abstract class JdbcTableExistsPredicate implements TableExistsPredicate {

    @Override
    public boolean test(DataSource dataSource, String tableName) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(getQuerySql());
            statement.setString(1, tableName);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    protected abstract String getQuerySql();

}