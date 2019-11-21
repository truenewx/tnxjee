package org.truenewx.tnxjee.web.view.tag;

import org.springframework.stereotype.Component;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;

/**
 * 判断是否没有错误消息的标签
 *
 * @author jianglei
 */
@Component
public class NoErrorTag extends HasErrorTag {

    @Override
    protected String getTagName() {
        return "no-error";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {
        if (hasError(context)) {
            handler.removeElement();
        } else {
            handler.removeTags();
        }
    }
}
