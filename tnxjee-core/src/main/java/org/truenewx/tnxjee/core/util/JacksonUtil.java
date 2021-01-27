package org.truenewx.tnxjee.core.util;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.truenewx.tnxjee.core.jackson.NonConcreteTypeResolverBuilder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Jackson序列化工具类
 */
public class JacksonUtil {

    private JacksonUtil() {
    }

    static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
    static final ObjectMapper CLASSED_MAPPER;

    static {
        DEFAULT_MAPPER.findAndRegisterModules();
        DEFAULT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 序列化时不输出null
        DEFAULT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); // 允许序列化空对象
        DEFAULT_MAPPER.enable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS); // 日期类型的Key转换为时间戳
        DEFAULT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 反序列化时允许未知属性

        // 默认的映射器初始化后再初始化带类型属性的映射器
        CLASSED_MAPPER = copyClassedMapper();
    }

    public static ObjectMapper copyDefaultMapper() {
        return DEFAULT_MAPPER.copy();
    }

    /**
     * 复制一个将对象类型写入JSON串的映射器
     *
     * @return 将对象类型写入JSON串的映射器
     */
    public static ObjectMapper copyClassedMapper() {
        return withClassProperty(copyDefaultMapper());
    }

    @JsonFilter("DynamicFilter")
    private interface DynamicFilter {
    }

    public static ObjectMapper buildMapper(PropertyFilter filter, Class<?>... beanClasses) {
        ObjectMapper mapper = copyDefaultMapper();
        withPropertyFilter(filter, mapper, beanClasses);
        return mapper;
    }

    public static void withPropertyFilter(PropertyFilter filter, ObjectMapper mapper, Class<?>[] beanClasses) {
        if (filter != null && beanClasses.length > 0) {
            for (Class<?> beanClass : beanClasses) {
                registerFilterable(mapper, beanClass);
            }
            String filterId = DynamicFilter.class.getAnnotation(JsonFilter.class).value();
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter(filterId, filter);
            mapper.setFilterProvider(filterProvider);
        }
    }

    private static void registerFilterable(ObjectMapper mapper, Class<?> beanClass) {
        if (mapper.addMixIn(beanClass, DynamicFilter.class) == null) { // 首次注册才考虑同时注册复合属性类型
            Collection<PropertyMeta> propertyMetas = ClassUtil.findPropertyMetas(beanClass, true, false, true,
                    (propertyType, propertyName) -> {
                        return ClassUtil.isComplex(propertyType);
                    });
            propertyMetas.forEach(meta -> {
                registerFilterable(mapper, meta.getType());
            });
        }
    }

    public static ObjectMapper withClassProperty(ObjectMapper mapper) {
        NonConcreteTypeResolverBuilder builder = new NonConcreteTypeResolverBuilder();
        builder.init(JsonTypeInfo.Id.CLASS, null).inclusion(JsonTypeInfo.As.PROPERTY);
        return mapper.setDefaultTyping(builder);
    }

    public static String getTypePropertyName() {
        return JsonTypeInfo.Id.CLASS.getDefaultPropertyName();
    }

    public static boolean isSerializableNonConcrete(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        if ((modifiers & (Modifier.INTERFACE | Modifier.ABSTRACT)) != 0) {
            return Serializable.class.isAssignableFrom(clazz);
        }
        return false;
    }

}
