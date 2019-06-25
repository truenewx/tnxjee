package org.truenewx.tnxjee.config;

import java.util.Map;

/**
 * 属性集提供者
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface PropertiesProvider {

    Map<String, String> getProperties();

}
