package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 字符串数组属性转换器
 *
 * @author jianglei
 */
@Converter
public class StringArrayAttributeConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        String s = null;
        if (attribute != null) {
            if (attribute.length == 0) {
                s = Strings.EMPTY;
            } else {
                s = StringUtils.join(attribute, Strings.COMMA);
            }
        }
        return s;
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        if (dbData != null) {
            if (StringUtils.isBlank(dbData)) {
                return new String[0];
            } else {
                return dbData.split(Strings.COMMA);
            }
        }
        return null;
    }

}
