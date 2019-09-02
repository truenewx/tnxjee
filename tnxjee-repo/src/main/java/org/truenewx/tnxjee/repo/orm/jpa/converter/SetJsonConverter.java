package org.truenewx.tnxjee.repo.orm.jpa.converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.truenewx.tnxjee.core.util.JsonUtil;

/**
 * Set-JSON字符串的属性转换器
 *
 * @author jianglei
 */
@Converter
public class SetJsonConverter implements AttributeConverter<Set<?>, String> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected Class<?> getComponentType() {
        return null;
    }

    protected String[] getExcludeProperties() {
        return null;
    }

    @Override
    public String convertToDatabaseColumn(Set<?> attribute) {
        String json = null;
        if (attribute != null) {
            Class<?> componentType = getComponentType();
            String[] excludeProperties = getExcludeProperties();
            if (componentType != null && ArrayUtils.isNotEmpty(excludeProperties)) {
                json = JsonUtil.toJson(attribute, componentType, excludeProperties);
            } else {
                json = JsonUtil.toJson(attribute);
            }
        }
        return json;
    }

    @Override
    public Set<?> convertToEntityAttribute(String dbData) {
        if (StringUtils.isNotBlank(dbData)) {
            if ("[]".equals(dbData)) {
                return new HashSet<>();
            }
            try {
                List<?> list;
                Class<?> componentType = getComponentType();
                if (componentType == null) {
                    list = JsonUtil.json2List(dbData);
                } else {
                    list = JsonUtil.json2List(dbData, componentType);
                }
                return new HashSet<>(list);
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
