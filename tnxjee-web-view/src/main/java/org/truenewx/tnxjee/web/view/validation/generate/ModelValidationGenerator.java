package org.truenewx.tnxjee.web.view.validation.generate;

import java.util.Locale;
import java.util.Map;

import org.truenewx.tnxjee.model.Model;

/**
 * 模型校验规则生成器
 *
 * @author jianglei
 */
public interface ModelValidationGenerator {

    Map<String, Map<String, Object>> generate(Class<? extends Model> modelClass, Locale locale);

    Map<String, Object> generate(Class<? extends Model> modelClass, Locale locale,
            String propertyName);

}
