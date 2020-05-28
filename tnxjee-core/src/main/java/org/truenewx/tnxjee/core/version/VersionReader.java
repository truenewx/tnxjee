package org.truenewx.tnxjee.core.version;

/**
 * 版本号读取器
 *
 * @author jianglei
 * 
 */
public interface VersionReader {

    /**
     * @param withBuild 是否带构建号
     * @return 版本号
     */
    String getVersion(boolean withBuild);

    /**
     * 获取当前版本号
     *
     * @return 当前版本号
     */
    String getVersion();

    /**
     * @return 构建号
     */
    String getBuild();

}
