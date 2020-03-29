package org.truenewx.tnxjee.web.view.validation.generate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.model.validation.config.ValidationConfiguration;
import org.truenewx.tnxjee.model.validation.config.ValidationConfigurationFactory;
import org.truenewx.tnxjee.model.validation.rule.ValidationRule;
import org.truenewx.tnxjee.web.view.validation.rule.mapper.ValidationRuleMapper;

/**
 * 属性校验规则生成器实现
 *
 * @author jianglei
 */
@Component
public class ModelValidationGeneratorImpl
        implements ModelValidationGenerator, ContextInitializedBean {

    private ValidationConfigurationFactory validationConfigurationFactory;
    private Map<Class<?>, ValidationRuleMapper<ValidationRule>> ruleMappers = new HashMap<>();

    @Autowired(required = false)
    public void setValidationConfigurationFactory(
            ValidationConfigurationFactory validationConfigurationFactory) {
        this.validationConfigurationFactory = validationConfigurationFactory;
    }

    @Override
    public Map<String, Map<String, Object>> generate(Class<? extends Model> modelClass,
            Locale locale) {
        Map<String, Map<String, Object>> validation = new LinkedHashMap<>();
        if (this.validationConfigurationFactory != null) {
            ValidationConfiguration configuration = this.validationConfigurationFactory
                    .getConfiguration(modelClass);
            if (configuration != null) {
                Set<String> propertyNames = configuration.getPropertyNames();
                for (String propertyName : propertyNames) {
                    validation.put(propertyName, generate(configuration, propertyName, locale));
                }
            }
        }
        return validation;
    }

    @Override
    public Map<String, Object> generate(Class<? extends Model> modelClass, Locale locale,
            String propertyName) {
        if (this.validationConfigurationFactory != null) {
            ValidationConfiguration configuration = this.validationConfigurationFactory
                    .getConfiguration(modelClass);
            if (configuration != null) {
                return generate(configuration, propertyName, locale);
            }
        }
        return null;
    }

    private Map<String, Object> generate(ValidationConfiguration configuration, String propertyName,
            Locale locale) {
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
}
