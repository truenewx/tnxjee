package org.truenewx.tnxjee.web.view.thymeleaf.processor;

import org.thymeleaf.processor.element.MatchingElementName;

/**
 * Thymeleaf自定义属性支持
 */
public abstract class ThymeleafAttributeSupport extends ThymeleafHtmlTagProcessor {

    @Override
    protected final String getTagName() {
        return null;
    }

    @Override
    public final MatchingElementName getMatchingElementName() {
        return super.getMatchingElementName();
    }
    
}
