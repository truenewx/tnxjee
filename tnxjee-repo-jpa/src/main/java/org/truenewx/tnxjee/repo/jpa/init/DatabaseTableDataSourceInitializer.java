package org.truenewx.tnxjee.repo.jpa.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.config.AppConstants;

/**
 * 使用数据表进行智能判断的数据初始化器
 */
public class DatabaseTableDataSourceInitializer extends SmartDataSourceInitializer {

    private String tableName;
    private String keyFiledName;
    private String valueFieldName;
    @Autowired
    private Environment environment;

    public DatabaseTableDataSourceInitializer(Resource root, String tableName, String keyFiledName,
            String valueFieldName) {
        super(root);
        this.tableName = tableName;
        this.keyFiledName = keyFiledName;
        this.valueFieldName = valueFieldName;
    }

    @Override
    protected boolean executable(Connection connection, String version) throws SQLException {
        String lastVersion = getLastVersion(connection);
        if (lastVersion == null) { // 找不到最近执行版本，则返回false以避免在开发人员不知情的情况下执行脚本
            return false;
        }
        return version.compareTo(lastVersion) > 0; // 比较版本高于最近执行版本才能执行
    }

    private String getLastVersion(Connection connection) throws SQLException {
        String lastVersion = null;
        String sql = "select " + this.valueFieldName + " from " + this.tableName + " where " + this.keyFiledName + "=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, getKey());
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            lastVersion = rs.getString(1);
        }
        rs.close();
        statement.close();
        return lastVersion;
    }

    private String getKey() {
        String appName = this.environment.getProperty(AppConstants.PROPERTY_SPRING_APP_NAME);
        return appName + ".sql_last_version";
    }

    @Override
    protected void updateVersion(Connection connection, String version) throws SQLException {
        String sql = "update " + this.tableName + " set " + this.valueFieldName + "=? where " + this.keyFiledName + "=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, version);
        statement.setString(2, getKey());
        statement.executeUpdate();
    }

}
