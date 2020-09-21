package org.truenewx.tnxjee.web.validation.rule.mapper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.validation.rule.RegexRule;

/**
 * 正则表达式规则的校验映射集生成器
 *
 * @author jianglei
 */
@Component
public class RegexRuleMapper implements ValidationRuleMapper<RegexRule>, MessageSourceAware {
    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Map<String, Object> toMap(RegexRule rule, Locale locale) {
        String message = rule.getMessage();
        if (StringUtils.isNotBlank(message) && message.startsWith("{") && message.endsWith("}")) {
            String code = message.substring(1, message.length() - 1);
            message = this.messageSource.getMessage(code, null, Strings.EMPTY, locale);
        }
        if (message == null) {
            message = Strings.EMPTY;
        } else {
            message = HtmlUtils.htmlEscape(message);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("regex", new String[]{ rule.getExpression(), message });
        return result;
    }

}
