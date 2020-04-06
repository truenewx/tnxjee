package org.truenewx.tnxjee.web.api.meta.model;

import org.truenewx.tnxjee.core.enums.EnumType;

import java.util.HashMap;
import java.util.Map;

/**
 * API模型属性元数据
 */
public class ApiModelPropertyMeta {

    private String propertyName;
    private Map<String, Object> validation = new HashMap<>();
    private EnumType enumType;

    public ApiModelPropertyMeta(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Map<String, Object> getValidation() {
        return this.validation;
    }

    public EnumType getEnumType() {
        return this.enumType;
    }

    public void setEnumType(EnumType enumType) {
        this.enumType = enumType;
    }

}
