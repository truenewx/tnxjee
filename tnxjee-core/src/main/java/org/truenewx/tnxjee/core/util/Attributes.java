package org.truenewx.tnxjee.core.util;

/**
 * 属性集
 */
public interface Attributes {

    String get(String key);

    <V> V get(String key, V defaultValue);

}
