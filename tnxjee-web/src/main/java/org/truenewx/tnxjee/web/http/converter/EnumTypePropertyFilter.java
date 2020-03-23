package org.truenewx.tnxjee.web.http.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.jackson.TypedPropertyFilter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 枚举类型属性过滤器
 */
public class EnumTypePropertyFilter extends TypedPropertyFilter {

    private Map<Class<?>, String[]> enumProperties = new HashMap<>();
    private EnumDictResolver enumDictResolver;

    public void setEnumDictResolver(EnumDictResolver enumDictResolver) {
        this.enumDictResolver = enumDictResolver;
    }

    public TypedPropertyFilter addCaptionEnumProperties(Class<?> beanClass, String... captionEnumProperties) {
        if (captionEnumProperties.length > 0) {
            this.enumProperties.put(beanClass, captionEnumProperties);
        }
        return this;
    }

    @Override
    public Class<?>[] getTypes() {
        Set<Class<?>> types = new HashSet<>(this.enumProperties.keySet());
        Collections.addAll(types, super.getTypes());
        return types.toArray(new Class<?>[types.size()]);
    }

    @Override
    public boolean isNotEmpty() {
        return super.isNotEmpty() && this.enumProperties.size() > 0;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
            throws Exception {
        super.serializeAsField(pojo, jgen, provider, writer);
        if (this.enumDictResolver != null && include(writer)) {
            Class<?> beanClass = writer.getMember().getDeclaringClass();
            String[] enumPropertyNames = this.enumProperties.get(beanClass);
            if (ArrayUtils.isNotEmpty(enumPropertyNames)) {
                String propertyName = writer.getName();
                if (ArrayUtils.contains(enumPropertyNames, propertyName)) {
                    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(beanClass, propertyName);
                    if (pd != null && pd.getPropertyType().isEnum()) {
                        Method readMethod = pd.getReadMethod();
                        if (readMethod != null) {
                            Enum<?> value = (Enum<?>) readMethod.invoke(pojo);
                            if (value != null) {
                                String caption = this.enumDictResolver.getText(value, provider.getLocale());
                                if (caption != null) {
                                    jgen.writeStringField(getCaptionPropertyName(propertyName), caption);
                                }
                            }
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
