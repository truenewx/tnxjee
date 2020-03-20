package org.truenewx.tnxjee.repo.validation.rule;

import org.apache.commons.lang3.StringUtils;

/**
 * 正则表达式规则
 *
 * @author jianglei
 * 
 */
public class RegexRule extends ValidationRule {
    /**
     * 正则表达式
     */
    private String expression;
    /**
     * 错误消息模板
     */
    private String message;

    /**
     * 用指定正则表达式构建
     *
     * @param value
     *            正则表达式
     * @param message
     *            校验不通过时显示的错误消息模板
     */
    public RegexRule(String expression, String message) {
        this.expression = expression;
        this.message = message;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(this.expression) && StringUtils.isBlank(this.message);
    }

}
