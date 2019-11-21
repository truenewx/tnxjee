package org.truenewx.tnxjee.web.view.tag;

import org.springframework.stereotype.Component;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.controller.exception.resolver.ResolvedBusinessError;
import org.truenewx.tnxjee.web.view.tagext.ErrorTagSupport;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;

import java.util.List;

/**
 * 判断是否存在错误消息的标签
 *
 * @author jianglei
 */
@Component
public class HasErrorTag extends ErrorTagSupport {

    @Override
    protected String getTagName() {
        return "has-error";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {
        if (hasError(context)) {
            handler.removeTags();
        } else {
            handler.removeElement();
        }
    }

    protected final boolean hasError(ThymeleafElementTagContext context) {
        List<ResolvedBusinessError> errors = getErrors(context);
        if (errors != null) {
            String field = context.getTagAttributeValue("field", Strings.ASTERISK);
            if (Strings.ASTERISK.equals(field)) {
                return errors.size() > 0;
            }
            for (ResolvedBusinessError error : errors) {
                if (field.equals(error.getField())) {
                    return true;
                }
            }
        }
        return false;
    }
}
