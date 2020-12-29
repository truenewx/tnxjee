package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.JsonUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Object-JSON字符串的属性转换器
 *
 * @author jianglei
 */
@Converter
public class ObjectToJsonAttributeConverter implements AttributeConverter<Object, String> {

    private ObjectMapper mapper = JsonUtil.copyNonConcreteAndCollectionMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        String json = null;
        if (attribute != null) {
            try {
                json = this.mapper.writeValueAsString(attribute);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return json;
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (StringUtils.isNotBlank(dbData)) {
            try {
                this.mapper.readValue(dbData, Object.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
