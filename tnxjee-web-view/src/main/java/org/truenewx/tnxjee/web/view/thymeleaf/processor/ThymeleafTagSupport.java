package org.truenewx.tnxjee.web.view.thymeleaf.processor;

import org.thymeleaf.processor.element.MatchingAttributeName;

/**
 * Thymeleaf自定义标签支持
 */
public abstract class ThymeleafTagSupport extends ThymeleafElementTagProcessor {

    @Override
    protected final String getAttributeName() {
        return null;
    }

    @Override
    public final MatchingAttributeName getMatchingAttributeName() {
        return super.getMatchingAttributeName();
    }

}
