package org.truenewx.tnxjee.core.i18n;

import java.util.Locale;

/**
 * 属性显示名称解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface PropertyCaptionResolver {

    String resolveCaption(Class<?> clazz, String propertyName, Locale locale);

}
