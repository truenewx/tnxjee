package org.truenewx.tnxjee.core.version;

/**
 * 版本号读取器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface VersionReader {

    /**
     *
     * @param withBuild
     *            是否带构建号
     * @return 版本号
     */
    String getVersion(boolean withBuild);

    /**
     *
     * @return 构建号
     */
    String getBuild();

}
