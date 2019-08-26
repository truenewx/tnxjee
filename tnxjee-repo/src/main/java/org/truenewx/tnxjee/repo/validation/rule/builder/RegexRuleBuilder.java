package org.truenewx.tnxjee.repo.validation.rule.builder;

import java.lang.annotation.Annotation;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.repo.validation.rule.RegexRule;

/**
 * 正则表达式规则构建器
 *
 * @author jianglei
 */
@Component
public class RegexRuleBuilder implements ValidationRuleBuilder<RegexRule> {
    /**
     * 默认消息
     */
    public static final String DEFAULT_MESSAGE = getDefaultMessage(Pattern.class);

    /**
     * 获取指定注解类型对应的默认消息
     *
     * @param annoClass 注解类型
     * @return 对应的默认消息
     */
    protected static String getDefaultMessage(Class<?> annoClass) {
        return StringUtils.join("{", annoClass.getName(), ".message}");
    }

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[] { Pattern.class };
    }

    @Override
    public void update(Annotation annotation, RegexRule rule) {
        if (annotation.annotationType() == Pattern.class) {
            Pattern pattern = (Pattern) annotation;
            String expression = pattern.regexp();
            if (StringUtils.isNotBlank(expression)) {
                rule.setExpression(expression);
            }
            String message = pattern.message();
            if (!DEFAULT_MESSAGE.equals(message)) {
                rule.setMessage(message);
            }
        }
    }

    @Override
    public RegexRule create(Annotation annotation) {
        if (annotation.annotationType() == Pattern.class) {
            Pattern pattern = (Pattern) annotation;
            return new RegexRule(pattern.regexp(), pattern.message());
        }
        return null;
    }

}
