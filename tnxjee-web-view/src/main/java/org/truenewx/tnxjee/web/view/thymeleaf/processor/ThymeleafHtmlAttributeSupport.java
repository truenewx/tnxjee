package org.truenewx.tnxjee.web.view.thymeleaf.processor;

import org.thymeleaf.processor.element.MatchingElementName;

/**
 * Thymeleaf自定义HTML属性支持
 */
public abstract class ThymeleafHtmlAttributeSupport extends ThymeleafElementTagProcessor {

    @Override
    protected final String getTagName() {
        return null;
    }

    @Override
    public final MatchingElementName getMatchingElementName() {
        return super.getMatchingElementName();
    }

}
