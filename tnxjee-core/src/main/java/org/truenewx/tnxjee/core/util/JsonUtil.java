package org.truenewx.tnxjee.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.truenewx.tnxjee.core.serializer.JsonDateCodec;
import org.truenewx.tnxjee.core.util.json.MultiPropertyPreFilter;
import org.truenewx.tnxjee.core.util.json.TypeSerializeFilter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * JSON工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JsonUtil {

    static {
        JsonDateCodec dateCodec = new JsonDateCodec();
        SerializeConfig.getGlobalInstance().put(Date.class, dateCodec);
        SerializeConfig.getGlobalInstance().put(java.sql.Date.class, dateCodec);
        SerializeConfig.getGlobalInstance().put(Timestamp.class, dateCodec);
        ParserConfig.getGlobalInstance().putDeserializer(Date.class, dateCodec);
        ParserConfig.getGlobalInstance().putDeserializer(java.sql.Date.class, dateCodec);
        ParserConfig.getGlobalInstance().putDeserializer(Timestamp.class, dateCodec);
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    /**
     * 获取JSON过滤器实例
     *
     * @param clazz             需进行属性排除的类
     * @param excludeProperties 需排除的属性
     * @return JSON过滤器实例
     */
    private static PropertyPreFilter getFilterInstance(Class<?> clazz,
            String... excludeProperties) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(clazz);
        Set<String> excludeList = filter.getExcludes();
        for (String exclude : excludeProperties) {
            excludeList.add(exclude);
        }
        return filter;
    }

    /**
     * 获取JSON过滤器实例
     *
     * @param filteredPropertiesMap 过滤属性映射集
     * @return JSON过滤器实例
     */
    private static PropertyPreFilter getFilterInstance(
            Map<Class<?>, FilteredTokens> filteredPropertiesMap) {
        MultiPropertyPreFilter filter = new MultiPropertyPreFilter();
        for (Entry<Class<?>, FilteredTokens> entry : filteredPropertiesMap.entrySet()) {
            FilteredTokens value = entry.getValue();
            filter.addFilteredProperties(entry.getKey(), value.getIncludes(), value.getExcludes());
        }
        return filter;
    }

    /**
     * 将指定任意类型的对象转换为JSON标准格式的字符串
     *
     * @param obj               对象
     * @param clazz             需进行属性排除的类
     * @param excludeProperties 需排除的属性
     * @return JSON格式的字符串
     */
    public static String toJson(Object obj, Class<?> clazz, String... excludeProperties) {
        return JSON.toJSONString(obj, getFilterInstance(clazz, excludeProperties));
    }

    /**
     * 将指定任意类型的对象转换为JSON标准格式的字符串
     *
     * @param obj                   对象
     * @param filteredPropertiesMap 过滤属性映射集
     * @return JSON格式的字符串
     */
    public static String toJson(Object obj, Map<Class<?>, FilteredTokens> filteredPropertiesMap) {
        return JSON.toJSONString(obj, getFilterInstance(filteredPropertiesMap));
    }

    /**
     * 将指定任意类型的对象转换为JSON标准格式的字符串
     *
     * @param obj               对象
     * @param appendTypeClasses 需要附加类型字段的类型集
     * @return JSON格式的字符串
     */
    public static String toJson(Object obj, Class<?>... appendTypeClasses) {
        if (appendTypeClasses.length == 0) {
            return JSON.toJSONString(obj);
        } else {
            return JSON.toJSONString(obj, new TypeSerializeFilter(appendTypeClasses));
        }
    }

    /**
     * 将JSON标准形式的字符串转换为对象
     *
     * @param json JSON标准形式的字符串
     * @return 转换形成的对象
     */
    public static Object json2Bean(String json) {
        return JSON.parseObject(json);
    }

    /**
     * 将JSON标准形式的字符串转换为指定类型的对象
     *
     * @param json  JSON标准形式的字符串
     * @param clazz 要转换的类型
     * @return 转换形成的对象
     */
    public static <T> T json2Bean(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 将JSON标准形式的字符串转换生成的对象的所有属性值覆盖指定对象的相应属性值
     *
     * @param json JSON标准形式的字符串
     * @param bean 要覆盖的对象
     */
    public static void jsonCoverBean(String json, Object bean) {
        JSONObject jsonObj = JSON.parseObject(json);
        for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
            try {
                BeanUtils.setProperty(bean, entry.getKey(), entry.getValue());
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } // 出现异常则忽略该属性的覆盖操作
        }
    }

    /**
     * 将JSON标准形式的字符串转换为具体类型不确定的List
     *
     * @param json JSON标准形式的字符串
     * @return 转换形成的对象List
     */
    public static List<Object> json2List(String json) {
        return JSON.parseArray(json);
    }

    /**
     * 将JSON标准形式的字符串转换为指定类型的对象List
     *
     * @param json  JSON标准形式的字符串
     * @param clazz 元素类型
     * @return 转换形成的对象List
     */
    public static <T> List<T> json2List(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * 将JSON标准形式的字符串转换为指定类型的对象List
     *
     * @param json  JSON标准形式的字符串
     * @param types 依次为结果数组的每一个元素的类型，为空则不限制类型
     * @return 转换形成的对象List
     */
    public static List<Object> json2List(String json, Type[] types) {
        if (types != null && types.length > 0) {
            return JSON.parseArray(json, types);
        } else {
            return JSON.parseArray(json);
        }
    }

    /**
     * 将JSON标准形式的字符串转换为Map
     *
     * @param json JSON标准形式的字符串
     * @return 转换形成的Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(String json) {
        return JSON.parseObject(json, LinkedHashMap.class);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> json2Map(String json, Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> result = new HashMap<>();
        Map<String, Object> map = JSON.parseObject(json);
        for (Entry<String, Object> entry : map.entrySet()) {
            K key;
            if (keyClass != String.class) {
                key = JSON.parseObject(entry.getKey(), keyClass);
            } else {
                key = (K) entry.getKey();
            }
            V value = (V) entry.getValue();
            result.put(key, value);
        }
        return result;
    }

    /**
     * 将JSON标准形式的字符串转换为Properties
     *
     * @param json JSON标准形式的字符串
     * @return 转换形成的Properties
     */
    public static Properties json2Properties(String json) {
        return JSON.parseObject(json, Properties.class);
    }

    /**
     * 将JSON标准形式的字符串转换为数组
     *
     * @param json JSON标准形式的字符串
     * @return 转换形成的数组
     */
    public static Object[] json2Array(String json) {
        JSONArray array = JSON.parseArray(json);
        return array == null ? null : array.toArray(new Object[array.size()]);
    }

    /**
     * 将JSON标准形式的字符串转换为数组
     *
     * @param json  JSON标准形式的字符串
     * @param clazz 元素类型
     * @return 转换形成的数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] json2Array(String json, Class<T> clazz) {
        List<T> list = JSON.parseArray(json, clazz);
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        int i = 0;
        for (T obj : list) {
            array[i++] = obj;
        }
        return array;
    }

}
