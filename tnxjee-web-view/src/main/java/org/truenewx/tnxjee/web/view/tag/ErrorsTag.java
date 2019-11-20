package org.truenewx.tnxjee.web.view.tag;

import org.springframework.stereotype.Component;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.controller.exception.resolver.ResolvedBusinessError;
import org.truenewx.tnxjee.web.view.tagext.ErrorTagSupport;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;

import java.util.List;

/**
 * 输出错误消息的标签
 *
 * @author jianglei
 */
@Component
public class ErrorsTag extends ErrorTagSupport {

    @Override
    protected String getTagName() {
        return "errors";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {
        StringBuffer message = new StringBuffer();
        List<ResolvedBusinessError> errors = getErrors(context);
        if (errors != null) {
            String field = context.getTagAttributeValue("field", Strings.ASTERISK);
            String delimiter = context.getTagAttributeValue("delimiter", "<br>");
            errors.forEach(error -> {
                if (Strings.ASTERISK.equals(field) || field.equals(error.getField())) {
                    message.append(delimiter).append(error.getMessage());
                }
            });
            if (message.length() > 0) {
                message.delete(0, delimiter.length());
            }
        }
        handler.replaceWith(message, false);
    }
}
