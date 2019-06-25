package org.truenewx.tnxjee.core.serializer;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 基于字符串的序列化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface StringSerializer {

    /**
     * 序列化Bean
     *
     * @param bean Bean
     * @return 序列化后的字符串
     */
    String serialize(Object bean);

    /**
     * 反序列化为Bean
     *
     * @param s    序列化字符串
     * @param type 期望类型
     * @return Bean
     */
    <T> T deserialize(String s, Class<T> type);

    /**
     * 反序列化为数组
     *
     * @param s            序列化字符串
     * @param elementTypes 数组元素类型集，依次为结果数组的每一个元素的类型，为空则不限制类型
     * @return 数组
     */
    Object[] deserializeArray(String s, Type... elementTypes);

    /**
     * 反序列化为集合
     *
     * @param s           序列化字符串
     * @param elementType 集合元素类型
     * @return 集合
     */
    <T> List<T> deserializeList(String s, Class<T> elementType);

}
