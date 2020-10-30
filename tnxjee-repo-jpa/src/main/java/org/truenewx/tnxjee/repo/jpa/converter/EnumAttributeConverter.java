package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.EnumUtils;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.spec.enums.support.EnumValueHelper;

/**
 * 枚举属性转换器
 *
 * @author jianglei
 */
@Converter
public class EnumAttributeConverter<T extends Enum<T>> implements AttributeConverter<T, String> {

    private Class<T> entityClass;

    protected Class<T> getEnumClass() {
        if (this.entityClass == null) {
            this.entityClass = ClassUtil.getActualGenericType(getClass(), 0);
        }
        return this.entityClass;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        String value = EnumValueHelper.getValue(attribute);
        if (value == null) {
            value = attribute.name();
        }
        return value;
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        T attribute = EnumValueHelper.valueOf(getEnumClass(), dbData);
        if (attribute == null) {
            attribute = EnumUtils.getEnum(getEnumClass(), dbData);
        }
        return attribute;
    }

}
