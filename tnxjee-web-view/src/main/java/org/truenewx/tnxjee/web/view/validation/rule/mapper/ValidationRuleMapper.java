package org.truenewx.tnxjee.web.view.validation.rule.mapper;

import java.util.Locale;
import java.util.Map;

import org.truenewx.tnxjee.model.validation.rule.ValidationRule;

/**
 * 校验规则映射集转换器
 *
 * @author jianglei
 * @param <R> 校验规则类型
 */
public interface ValidationRuleMapper<R extends ValidationRule> {

    /**
     * 将指定校验规则生成为校验规则集
     *
     * @param rule   校验规则
     * @param locale 区域
     * @return 校验规则集
     */
    Map<String, Object> toMap(R rule, Locale locale);
}
