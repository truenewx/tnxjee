package org.truenewx.tnxjee.web.view.tag;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafHtmlTag;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafTagSupport;

/**
 * 格式化输出标签
 *
 * @author jianglei
 */
@Component
public class FormatTag extends ThymeleafTagSupport {

    @Override
    protected String getTagName() {
        return "format";
    }

    @Override
    protected void doProcess(ThymeleafHtmlTag tag,
            IElementTagStructureHandler handler) {
        Object value = tag.getAttributeValue("value");
        String pattern = tag.getAttributeValue("pattern");
        String result = null;
        if (value instanceof Date) {
            if (pattern == null) {
                pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = DateUtil.format((Date) value, pattern);
        } else if (value instanceof Instant) {
            if (pattern == null) {
                pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = TemporalUtil.format((Instant) value, pattern);
        } else if (value instanceof LocalDate) {
            if (pattern == null) {
                pattern = DateUtil.SHORT_DATE_PATTERN;
            }
            result = TemporalUtil.format((LocalDate) value, pattern);
        } else if (value instanceof LocalTime) {
            if (pattern == null) {
                pattern = DateUtil.TIME_PATTERN;
            }
            result = TemporalUtil.format((LocalTime) value, pattern);
        } else if (value instanceof LocalDateTime) {
            if (pattern == null) {
                pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = TemporalUtil.format((LocalDateTime) value, pattern);
        }
        if (result != null) {
            handler.replaceWith(result, false);
        }
    }
}
