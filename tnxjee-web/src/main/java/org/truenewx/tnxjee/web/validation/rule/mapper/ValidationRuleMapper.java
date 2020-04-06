package org.truenewx.tnxjee.web.validation.rule.mapper;

import org.truenewx.tnxjee.model.validation.rule.ValidationRule;

import java.util.Locale;
import java.util.Map;

/**
 * 校验规则映射集转换器
 *
 * @param <R> 校验规则类型
 * @author jianglei
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
