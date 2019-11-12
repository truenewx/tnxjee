package org.truenewx.tnxjee.web.view.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;

/**
 * Thymeleaf的HTML元素标签处理器
 */
public abstract class ThymeleafHtmlTagProcessor extends AbstractProcessor
        implements IElementTagProcessor, ThymeleafProcessor {

    public static final int PRECEDENCE = 1000;

    private MatchingElementName matchingElementName;
    private MatchingAttributeName matchingAttributeName;

    public ThymeleafHtmlTagProcessor() {
        super(TemplateMode.HTML, PRECEDENCE);

        TemplateMode templateMode = getTemplateMode();
        String dialectPrefix = getDialectPrefix();
        String tagName = getTagName();
        if (tagName != null) {
            this.matchingElementName = MatchingElementName.forElementName(templateMode,
                    ElementNames.forName(templateMode, dialectPrefix, tagName));
        }
        String attributeName = getAttributeName();
        if (attributeName != null) {
            this.matchingAttributeName = MatchingAttributeName.forAttributeName(templateMode,
                    AttributeNames.forName(templateMode, dialectPrefix, attributeName));
        }
    }

    protected abstract String getTagName();

    protected abstract String getAttributeName();

    @Override
    public MatchingElementName getMatchingElementName() {
        return this.matchingElementName;
    }

    @Override
    public MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }

    public final void process(ITemplateContext context, IProcessableElementTag tag,
            IElementTagStructureHandler structureHandler) {
        try {
            doProcess(new ThymeleafElementTagContext(context, tag), structureHandler);
        } catch (TemplateProcessingException e) {
            // This is a nice moment to check whether the execution raised an error and, if so, add location information
            if (tag.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(tag.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(tag.getLine(), tag.getCol());
                }
            }
            throw e;
        } catch (Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + getClass().getName() + "'",
                    tag.getTemplateName(), tag.getLine(), tag.getCol(), e);
        }
    }

    protected abstract void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler);

}

