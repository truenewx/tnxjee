package org.truenewx.tnxjee.web.view.validation.generate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.model.Model;

/**
 * 处理器校验规则填充者实现
 *
 * @author jianglei
 */
@Component
public class HandlerValidationApplierImpl implements HandlerValidationApplier {
    /**
     * 传输模型校验属性名称.
     */
    private static String VALIDATION_ATTRIBUTE_NAME = "validation";
    /**
     * 类名关键字
     */
    private static String CLASS_NAME = "@type";

    private ModelValidationGenerator modelValidationGenerator;

    @Autowired
    public void setModelValidationGenerator(ModelValidationGenerator modelValidationGenerator) {
        this.modelValidationGenerator = modelValidationGenerator;
    }

    @Override
    public void applyValidation(ModelAndView mav, Class<? extends Model>[] modelClasses,
            Locale locale) {
        if (modelClasses.length > 0 && mav != null) {
            Map<String, Map<String, String>> validations = new HashMap<>(); // 模型类名-属性名-规则表达式
            for (Class<? extends Model> modelClass : modelClasses) {
                Map<String, Map<String, Object>> modelValidation = this.modelValidationGenerator
                        .generate(modelClass, locale);
                if (modelValidation.size() > 0) {
                    Map<String, String> expressionMap = toExpressionMap(modelValidation);
                    expressionMap.put(CLASS_NAME, modelClass.getName()); // 存放类全名，以便于后续处理

                    // 如果存在重复的类简名，则移除以类简名为关键字的表达式，使用类全名关键字
                    String classSimpleName = modelClass.getSimpleName();
                    Map<String, String> oldMap = validations.put(classSimpleName, expressionMap);
                    if (oldMap != null) {
                        validations.remove(classSimpleName);
                        validations.put(oldMap.get(CLASS_NAME), oldMap);
                        validations.put(expressionMap.get(CLASS_NAME), expressionMap);
                    }
                }
            }
            Map<String, String> validation = new HashMap<>();
            if (validations.size() > 0) {
                // 添加第一个模型类的校验属性映射
                validation.putAll(validations.values().iterator().next());

                if (validations.size() > 1) { // 多个模型类时，添加模型类的属性名加上类名前缀的校验属性映射
                    for (Entry<String, Map<String, String>> entry : validations.entrySet()) {
                        String className = entry.getKey();
                        for (Entry<String, String> e : entry.getValue().entrySet()) {
                            String propertyName = e.getKey();
                            if (!CLASS_NAME.equals(propertyName)) {
                                propertyName = StringUtils.join(className, Strings.DOT,
                                        propertyName); // 属性名加上类名前缀
                                validation.put(propertyName, e.getValue());
                            }
                        }
                    }
                }
            }
            mav.addObject(VALIDATION_ATTRIBUTE_NAME, validation);
        }
    }

    private Map<String, String> toExpressionMap(Map<String, Map<String, Object>> modelValidation) {
        Map<String, String> expressions = new LinkedHashMap<>();
        for (Entry<String, Map<String, Object>> entry : modelValidation.entrySet()) {
            String expression = JsonUtil.toJson(entry.getValue()).replace('"', '\'');
            expressions.put(entry.getKey(), expression);
        }
        return expressions;
    }

}
