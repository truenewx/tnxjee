package org.truenewx.tnxjee.webmvc.api.meta;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.*;

import javax.validation.constraints.Email;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumSub;
import org.truenewx.tnxjee.core.enums.EnumType;
import org.truenewx.tnxjee.core.i18n.PropertyCaptionResolver;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.model.validation.config.ValidationConfiguration;
import org.truenewx.tnxjee.model.validation.config.ValidationConfigurationFactory;
import org.truenewx.tnxjee.model.validation.rule.ValidationRule;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiModelPropertyMeta;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiModelPropertyType;
import org.truenewx.tnxjee.webmvc.validation.rule.mapper.ValidationRuleMapper;

/**
 * 属性校验规则生成器实现
 *
 * @author jianglei
 */
@Component
public class ApiModelMetaResolverImpl implements ApiModelMetaResolver, ContextInitializedBean {

    private static final Class<?>[] INTEGER_CLASSES = { long.class, int.class, short.class,
            byte.class, Long.class, Integer.class, Short.class, Byte.class, BigInteger.class };
    private static final Class<?>[] DECIMAL_CLASSES = { double.class, float.class, Double.class,
            Float.class, BigDecimal.class };

    @Autowired(required = false) // 如果工程未依赖tnxjee-repo-jpa，则可能没有该bean
    private ValidationConfigurationFactory validationConfigurationFactory;
    @Autowired
    private EnumDictResolver enumDictResolver;
    @Autowired
    private PropertyCaptionResolver propertyCaptionResolver;
    private Map<Class<?>, ValidationRuleMapper<ValidationRule>> ruleMappers = new HashMap<>();

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, ValidationRuleMapper> beans = context
                .getBeansOfType(ValidationRuleMapper.class);
        for (ValidationRuleMapper<ValidationRule> ruleGenerator : beans.values()) {
            Class<?> ruleClass = ClassUtil.getActualGenericType(ruleGenerator.getClass(),
                    ValidationRuleMapper.class, 0);
            this.ruleMappers.put(ruleClass, ruleGenerator);
        }
    }

    @Override
    @Cacheable("ApiModelMeta")
    public Map<String, ApiModelPropertyMeta> resolve(Class<? extends Model> modelClass,
            Locale locale) {
        Map<String, ApiModelPropertyMeta> metas = new HashMap<>();
        if (this.validationConfigurationFactory != null) {
            ValidationConfiguration configuration = this.validationConfigurationFactory
                    .getConfiguration(modelClass);
            ClassUtil.loopFields(modelClass, null, field -> {
                String propertyName = field.getName();
                String caption = this.propertyCaptionResolver.resolveCaption(modelClass,
                        propertyName, locale);
                if (propertyName.equals(caption)
                        && !Locale.ENGLISH.getLanguage().equals(locale.getLanguage())) {
                    caption = null;
                }
                ApiModelPropertyType type = getType(field);
                ApiModelPropertyMeta meta = new ApiModelPropertyMeta(caption, type);
                Map<String, Object> validation = getValidation(configuration, propertyName, locale);
                if (validation.size() > 0) {
                    meta.setValidation(validation);
                }
                Class<?> fieldType = field.getType();
                if (fieldType.isEnum()) {
                    EnumSub enumSub = field.getAnnotation(EnumSub.class);
                    String subtype = enumSub == null ? null : enumSub.value();
                    EnumType enumType = this.enumDictResolver.getEnumType(fieldType.getName(), subtype, locale);
                    if (enumType != null) {
                        meta.setEnums(enumType.getItems());
                    }
                }
                metas.put(propertyName, meta);
                return true;
            });
        }
        return metas;
    }

    private ApiModelPropertyType getType(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType == String.class) {
            if (field.getAnnotation(Email.class) != null) {
                return ApiModelPropertyType.EMAIL;
            }
            if (field.getAnnotation(URL.class) != null) {
                return ApiModelPropertyType.EMAIL;
            }
            return ApiModelPropertyType.TEXT;
        } else {
            if (fieldType == boolean.class || fieldType == Boolean.class) {
                return ApiModelPropertyType.BOOLEAN;
            }
            if (ArrayUtils.contains(INTEGER_CLASSES, fieldType)) {
                return ApiModelPropertyType.INTEGER;
            }
            if (ArrayUtils.contains(DECIMAL_CLASSES, fieldType)) {
                return ApiModelPropertyType.DECIMAL;
            }
            if (fieldType.isArray()) { // 字符串数组或枚举数组为多选选项型
                Class<?> componentType = fieldType.getComponentType();
                if (componentType == String.class || componentType.isArray()) {
                    return ApiModelPropertyType.OPTION;
                }
            }
            // 枚举或日期类型为单选选项型
            if (fieldType.isEnum() || Temporal.class.isAssignableFrom(fieldType)
                    || Date.class.isAssignableFrom(fieldType)) {
                return ApiModelPropertyType.OPTION;
            }
        }
        return null;
    }

    private Map<String, Object> getValidation(ValidationConfiguration configuration,
            String propertyName, Locale locale) {
        Map<String, Object> validation = new LinkedHashMap<>(); // 保留顺序
        Set<ValidationRule> rules = configuration.getRules(propertyName);
        for (ValidationRule rule : rules) {
            ValidationRuleMapper<ValidationRule> ruleMapper = this.ruleMappers.get(rule.getClass());
            if (ruleMapper != null) {
                Map<String, Object> ruleMap = ruleMapper.toMap(rule, locale);
                if (ruleMap != null) {
                    validation.putAll(ruleMap);
                }
            }
        }
        return validation;
    }
}
