package org.truenewx.tnxjee.web.view.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;
import org.truenewx.tnxjee.web.view.thymeleaf.dialect.ThymeleafDialect;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ProcessableElementTag;

/**
 * 抽象的元素标签处理器
 */
public abstract class AbstractElementTagProcessor extends AbstractProcessor
        implements IElementTagProcessor, ThymeleafProcessor {

    public static final int PRECEDENCE = 1000;

    private String dialectPrefix;
    private MatchingElementName matchingElementName;

    public AbstractElementTagProcessor(String dialectPrefix, String elementName) {
        super(TemplateMode.HTML, PRECEDENCE);

        this.dialectPrefix = dialectPrefix;
        TemplateMode templateMode = getTemplateMode();
        if (elementName != null) {
            this.matchingElementName = MatchingElementName.forElementName(templateMode,
                    ElementNames.forName(templateMode, this.dialectPrefix, elementName));
        }
    }

    public AbstractElementTagProcessor(String elementName) {
        this(ThymeleafDialect.PREFIX, elementName);
    }

    @Override
    public final String getDialectPrefix() {
        return this.dialectPrefix;
    }

    @Override
    public final MatchingElementName getMatchingElementName() {
        return this.matchingElementName;
    }

    @Override
    public MatchingAttributeName getMatchingAttributeName() {
        return null;
    }

    public final void process(ITemplateContext context, IProcessableElementTag tag,
            IElementTagStructureHandler structureHandler) {
        try {
            doProcess(context, new ProcessableElementTag(tag), structureHandler);
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

    protected abstract void doProcess(ITemplateContext context, ProcessableElementTag tag,
            IElementTagStructureHandler structureHandler);
}

