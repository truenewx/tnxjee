package org.truenewx.tnxjee.core.spring.util;

import org.springframework.util.StringValueResolver;

/**
 * 占位符解决器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public interface PlaceholderResolver extends StringValueResolver,
                org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver {

    /**
     * 替换指定字符串中的占位符，返回替换之后的字符串
     * 
     * @param strVal
     *            要替换的字符串
     * @return 替换完毕后的字符串
     */
    @Override
    String resolveStringValue(String strVal);

    /**
     * 获取指定占位符关键字对应的文本
     * 
     * @param placeholderKey
     *            占位符关键字，不包含${和}
     * @return 指定占位符关键字对应的文本
     */
    @Override
    String resolvePlaceholder(String placeholderKey);

    /**
     * 获取所有的占位符关键字（不包含${和}）清单
     * 
     * @return 所有的占位符关键字（不包含${和}）清单
     */
    Iterable<String> getPlaceholderKeys();

}