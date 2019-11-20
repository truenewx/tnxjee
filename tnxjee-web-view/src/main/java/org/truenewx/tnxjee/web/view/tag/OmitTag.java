package org.truenewx.tnxjee.web.view.tag;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafHtmlTagSupport;

/**
 * 截取字符串标签
 *
 * @author jianglei
 */
@Component
public class OmitTag extends ThymeleafHtmlTagSupport {

    private final static String REPLACE_OPERATOR = "...";

    @Override
    protected String getTagName() {
        return "omit";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context, IElementTagStructureHandler handler) {
        String value = context.getTagAttributeValue("value");
        if (StringUtils.isNotEmpty(value)) {
            int size = context.getTagAttributeValue("size", 0);
            if (0 < size && size < value.length()) {
                value = value.substring(0, size - 1) + REPLACE_OPERATOR;
            }
        }
        handler.replaceWith(value, false);
    }
}
