package org.truenewx.tnxjee.web.api.meta;

import org.truenewx.tnxjee.core.enums.EnumType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * API模型元数据
 */
public class ApiModelMeta {

    private Map<String, Map<String, Object>> validations = new HashMap<>();

    public void setValidation(String propertyName, Map<String, Object> validation) {
        if (validation != null) {
            this.validations.computeIfAbsent(propertyName, v -> {
                return new HashMap<>();
            }).putAll(validation);
        } else {
            this.validations.remove(propertyName);
        }
    }

    public void setEnumType(String propertyName, EnumType enumType) {
        this.validations.computeIfAbsent(propertyName, v -> {
            return new HashMap<>();
        }).put("enums", enumType.getItems());
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(this.validations);
    }

}
