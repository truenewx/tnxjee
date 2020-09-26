package org.truenewx.tnxjee.webmvc.http.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.jackson.TypedPropertyFilter;

import java.util.*;

/**
 * 枚举类型属性过滤器
 */
public class EnumTypePropertyFilter extends TypedPropertyFilter {

    private Map<Class<?>, String[]> pureEnumProperties = new HashMap<>();
    private EnumDictResolver enumDictResolver;

    public void setEnumDictResolver(EnumDictResolver enumDictResolver) {
        this.enumDictResolver = enumDictResolver;
    }

    public TypedPropertyFilter addPureEnumProperties(Class<?> beanClass, String... pureEnumProperties) {
        if (pureEnumProperties.length > 0) {
            this.pureEnumProperties.put(beanClass, pureEnumProperties);
        }
        return this;
    }

    @Override
    public Class<?>[] getTypes() {
        Set<Class<?>> types = new HashSet<>(this.pureEnumProperties.keySet());
        Collections.addAll(types, super.getTypes());
        return types.toArray(new Class<?>[types.size()]);
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
            throws Exception {
        super.serializeAsField(pojo, jgen, provider, writer);
        if (this.enumDictResolver != null && writer.getType().isEnumType()) { // 附加处理枚举类型
            AnnotatedMember member = writer.getMember();
            Enum<?> value = (Enum<?>) member.getValue(pojo);
            if (value != null) {
                Class<?> beanClass = member.getDeclaringClass();
                String[] pureEnumPropertyNames = this.pureEnumProperties.get(beanClass);
                if (ArrayUtils.isNotEmpty(pureEnumPropertyNames)) {
                    String propertyName = writer.getName();
                    if (!ArrayUtils.contains(pureEnumPropertyNames, propertyName)) {
                        String caption = this.enumDictResolver.getText(value, provider.getLocale());
                        if (caption != null) {
                            jgen.writeStringField(getCaptionPropertyName(propertyName), caption);
                        }
                    }
                }
            }
        }
    }

    protected String getCaptionPropertyName(String enumPropertyName) {
        return enumPropertyName + Strings.UNDERLINE + "caption";
    }
}
