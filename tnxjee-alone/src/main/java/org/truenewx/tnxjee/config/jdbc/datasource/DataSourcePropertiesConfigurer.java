package org.truenewx.tnxjee.config.jdbc.datasource;

import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.config.PropertiesConfigurer;

/**
 * 数据源属性配置器
 *
 * @author jianglei
 * 
 */
public class DataSourcePropertiesConfigurer extends PropertiesConfigurer {

    private String propertyPrefix = "";
    private String serverName;
    private String port;
    private String databaseName;
    private String username;
    private String password;

    public DataSourcePropertiesConfigurer() {
        setProperties(new HashMap<>());
    }

    public void setPropertyPrefix(final String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    }

    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public Map<String, String> getProperties() {
        final Map<String, String> properties = super.getProperties();
        properties.put(this.propertyPrefix + "serverName", this.serverName);
        properties.put(this.propertyPrefix + "port", this.port);
        properties.put(this.propertyPrefix + "databaseName", this.databaseName);
        properties.put(this.propertyPrefix + "username", this.username);
        properties.put(this.propertyPrefix + "password", this.password);
        return properties;
    }

}
