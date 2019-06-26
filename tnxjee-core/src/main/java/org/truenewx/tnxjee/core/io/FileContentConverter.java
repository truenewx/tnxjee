package org.truenewx.tnxjee.core.io;

/**
 * 文件内容转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface FileContentConverter {

    void convert(String locationPattern, String encoding);

}
