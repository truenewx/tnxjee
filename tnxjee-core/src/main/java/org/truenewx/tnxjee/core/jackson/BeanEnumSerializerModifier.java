package org.truenewx.tnxjee.core.jackson;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumItemKey;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.CollectionUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * Bean枚举序列化器修改器
 */
public class BeanEnumSerializerModifier extends BeanSerializerModifier {

    private EnumDictResolver enumDictResolver;
    private Map<Class<?>, Collection<String>> ignoredPropertyNamesMapping = new HashMap<>();

    public BeanEnumSerializerModifier(EnumDictResolver enumDictResolver) {
        this.enumDictResolver = enumDictResolver;
    }

    public void addIgnoredPropertiesNames(Class<?> beanClass, String... ignoredPropertyNames) {
        if (ignoredPropertyNames.length > 0) {
            Collection<String> names = this.ignoredPropertyNamesMapping
                    .computeIfAbsent(beanClass, k -> new HashSet<>());
            CollectionUtil.addAll(names, ignoredPropertyNames);
        }
    }

    private boolean isIgnored(Object bean, String propertyName) {
        if (bean != null) {
            Collection<String> ignoredPropertyNames = this.ignoredPropertyNamesMapping.get(bean.getClass());
            return ignoredPropertyNames != null && ignoredPropertyNames.contains(propertyName);
        }
        return false;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
            List<BeanPropertyWriter> beanProperties) {
        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);
            // 属性类型为枚举类型，或属性具有@EnumItemKey注解，都需要考虑附加caption字段
            JavaType propertyType = writer.getType();
            JavaType contentType = propertyType.getContentType();
            EnumItemKey enumItemKey = writer.getAnnotation(EnumItemKey.class);
            if (propertyType.isEnumType() || (propertyType.isArrayType() && contentType.isEnumType())
                    || (propertyType.getRawClass() == String.class && enumItemKey != null)) {
                beanProperties.set(i, new BeanPropertyWriter(writer) {

                    private static final long serialVersionUID = 6267157125639776096L;

                    @Override
                    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
                            throws Exception {
                        super.serializeAsField(bean, gen, prov);
                        String propertyName = getName();
                        if (!isIgnored(bean, propertyName)) {
                            AnnotatedMember member = getMember();
                            if (propertyType.isArrayType()) {
                                if (contentType.isEnumType()) {
                                    Enum<?>[] array = (Enum<?>[]) member.getValue(bean);
                                    if (ArrayUtils.isNotEmpty(array)) {
                                        Map<String, Object> map = new HashMap<>();
                                        for (Enum<?> value : array) {
                                            String caption = BeanEnumSerializerModifier.this.enumDictResolver
                                                    .getText(value, prov.getLocale());
                                            if (caption != null) {
                                                map.put(value.name(), caption);
                                            }
                                        }
                                        if (map.size() > 0) {
                                            gen.writeObjectField(BeanUtil.getEnumCaptionPropertyName(propertyName),
                                                    map);
                                        }
                                    }
                                }
                            } else {
                                String caption = null;
                                if (propertyType.isEnumType()) {
                                    Enum<?> value = (Enum<?>) member.getValue(bean);
                                    if (value != null) {
                                        caption = BeanEnumSerializerModifier.this.enumDictResolver
                                                .getText(value, prov.getLocale());
                                    }
                                } else if (enumItemKey != null) {
                                    String value = (String) member.getValue(bean);
                                    if (value != null) {
                                        caption = BeanEnumSerializerModifier.this.enumDictResolver
                                                .getText(enumItemKey.type(), enumItemKey.subtype(), value,
                                                        prov.getLocale());
                                    }
                                }
                                if (caption != null) {
                                    gen.writeStringField(BeanUtil.getEnumCaptionPropertyName(propertyName), caption);
                                }
                            }

                        }
                    }

                });
            }
        }
        return beanProperties;
    }

}
