package org.truenewx.tnxjee.repo.jpa.init;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.Profiles;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;

/**
 * 智能数据源初始化器
 */
public abstract class SmartDataSourceInitializer {

    private static final String TYPE_SCHEMA = "schema";
    private static final String TYPE_DATA = "data";

    @Autowired
    private ProfileSupplier profileSupplier;
    private Resource root;

    public SmartDataSourceInitializer(Resource root) {
        this.root = root;
    }

    public boolean isDisabled() {
        return Profiles.JUNIT.equals(this.profileSupplier.get());
    }

    public void execute(DataSource dataSource) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            Map<String, List<Resource>> mapping = getVersionResourcesMapping(connection);
            if (mapping.size() > 0) {
                String lastVersion = null;
                for (Map.Entry<String, List<Resource>> entry : mapping.entrySet()) {
                    Resource[] resources = entry.getValue().toArray(new Resource[0]);
                    DatabasePopulator databasePopulator = new ResourceDatabasePopulator(resources);
                    databasePopulator.populate(connection);
                    lastVersion = entry.getKey();
                }
                if (lastVersion != null) {
                    updateVersion(connection, lastVersion);
                }
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    protected Map<String, List<Resource>> getVersionResourcesMapping(Connection connection) {
        try {
            Map<String, List<Resource>> mapping = new TreeMap<>();
            File rootFile = this.root.getFile();
            if (rootFile.exists() && rootFile.canRead()) {
                File[] dirs = rootFile.listFiles(File::isDirectory);
                if (dirs != null) {
                    // 以版本目录的顺序依次执行各版本的脚本文件
                    for (File dir : dirs) {
                        String version = dir.getName();
                        if (executable(connection, version)) {
                            List<Resource> resources = new ArrayList<>();
                            // 同一个版本目录下，优先考虑执行schema再考虑执行data
                            addResourceTo(dir, TYPE_SCHEMA, resources);
                            addResourceTo(dir, TYPE_DATA, resources);
                            if (resources.size() > 0) {
                                mapping.put(version, resources);
                            }
                        }
                    }
                }
            }
            return mapping;
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
            return Collections.emptyMap();
        }
    }

    private void addResourceTo(File dir, String type, List<Resource> resources) {
        File file = new File(dir, type + ".sql");
        if (file.exists() && file.canRead()) {
            resources.add(new FileSystemResource(file));
        }
    }

    protected abstract boolean executable(Connection connection, String version) throws SQLException;

    protected abstract void updateVersion(Connection connection, String version) throws SQLException;

}
