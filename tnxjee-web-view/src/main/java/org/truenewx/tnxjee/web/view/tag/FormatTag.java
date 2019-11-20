package org.truenewx.tnxjee.web.view.tag;

import org.springframework.stereotype.Component;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafHtmlTagSupport;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 格式化输出标签
 *
 * @author jianglei
 */
@Component
public class FormatTag extends ThymeleafHtmlTagSupport {

    @Override
    protected String getTagName() {
        return "format";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {
        Object value = context.getTagAttributeValue("value");
        String pattern = context.getTagAttributeValue("pattern");
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
        } else if (value instanceof Number) {
            if (pattern == null) {
                pattern = "0.##";
            }
            result = new DecimalFormat(pattern).format(value);
        }
        if (result != null) {
            handler.replaceWith(result, false);
        }
    }
}
