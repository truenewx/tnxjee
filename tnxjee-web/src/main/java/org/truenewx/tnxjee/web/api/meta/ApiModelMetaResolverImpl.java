package org.truenewx.tnxjee.web.api.meta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumType;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.model.validation.config.ValidationConfiguration;
import org.truenewx.tnxjee.model.validation.config.ValidationConfigurationFactory;
import org.truenewx.tnxjee.model.validation.rule.ValidationRule;
import org.truenewx.tnxjee.web.api.meta.model.ApiModelPropertyMeta;
import org.truenewx.tnxjee.web.validation.rule.mapper.ValidationRuleMapper;

import java.util.*;

/**
 * 属性校验规则生成器实现
 *
 * @author jianglei
 */
@Component
public class ApiModelMetaResolverImpl implements ApiModelMetaResolver, ContextInitializedBean {

    @Autowired(required = false)
    private ValidationConfigurationFactory validationConfigurationFactory;
    @Autowired
    private EnumDictResolver enumDictResolver;
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
        return Collections.unmodifiableMap(generateMeta(modelClass, locale));
    }

    private Map<String, ApiModelPropertyMeta> generateMeta(Class<? extends Model> modelClass,
            Locale locale) {
        Map<String, ApiModelPropertyMeta> metas = new HashMap<>();
        if (this.validationConfigurationFactory != null) {
            ValidationConfiguration configuration = this.validationConfigurationFactory
                    .getConfiguration(modelClass);
            if (configuration != null) {
                Set<String> propertyNames = configuration.getPropertyNames();
                for (String propertyName : propertyNames) {
                    Map<String, Object> validation = generateValidation(configuration, propertyName,
                            locale);
                    if (validation.size() > 0) {
                        ApiModelPropertyMeta meta = new ApiModelPropertyMeta(propertyName);
                        meta.getValidation().putAll(validation);
                        metas.put(propertyName, meta);
                    }
                }
            }
        }
        ClassUtil.loopFields(modelClass, Enum.class, field -> {
            EnumType enumType = this.enumDictResolver.getEnumType(field.getType().getName(),
                    locale);
            if (enumType != null) {
                String propertyName = field.getName();
                ApiModelPropertyMeta meta = metas.get(propertyName);
                if (meta == null) {
                    meta = new ApiModelPropertyMeta(propertyName);
                    metas.put(propertyName, meta);
                }
                meta.setEnumType(enumType);
            }
            return true;
        });
        return metas;
    }

    private Map<String, Object> generateValidation(ValidationConfiguration configuration,
            String propertyName, Locale locale) {
        Map<String, Object> result = new LinkedHashMap<>(); // 保留顺序
        Set<ValidationRule> rules = configuration.getRules(propertyName);
        for (ValidationRule rule : rules) {
            ValidationRuleMapper<ValidationRule> ruleMapper = this.ruleMappers.get(rule.getClass());
            if (ruleMapper != null) {
                Map<String, Object> ruleMap = ruleMapper.toMap(rule, locale);
                if (ruleMap != null) {
                    result.putAll(ruleMap);
                }
            }
        }
        return result;
    }
}
