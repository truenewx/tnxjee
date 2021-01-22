package org.truenewx.tnxjee.core.jackson;

import java.util.*;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.util.CollectionUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
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
            if (writer.getType().isEnumType()) {
                beanProperties.set(i, new BeanPropertyWriter(writer) {

                    private static final long serialVersionUID = 6267157125639776096L;

                    @Override
                    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
                            throws Exception {
                        super.serializeAsField(bean, gen, prov);
                        String propertyName = getName();
                        if (!isIgnored(bean, propertyName)) {
                            AnnotatedMember member = getMember();
                            Enum<?> value = (Enum<?>) member.getValue(bean);
                            if (value != null) {
                                String caption = BeanEnumSerializerModifier.this.enumDictResolver
                                        .getText(value, prov.getLocale());
                                if (caption != null) {
                                    gen.writeStringField(getEnumCaptionPropertyName(propertyName), caption);
                                }
                            }
                        }
                    }

                });
            }
        }
        return beanProperties;
    }

    protected String getEnumCaptionPropertyName(String enumPropertyName) {
        return enumPropertyName + Strings.UNDERLINE + "caption";
    }

}
