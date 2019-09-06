package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.truenewx.tnxjee.core.enums.support.EnumValueHelper;
import org.truenewx.tnxjee.core.util.ClassUtil;

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
        return EnumValueHelper.getValue(attribute);
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        return EnumValueHelper.valueOf(getEnumClass(), dbData);
    }

}
