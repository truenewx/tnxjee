package org.truenewx.tnxjee.web.view.thymeleaf.model;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.truenewx.tnxjee.core.util.AttributeMap;
import org.truenewx.tnxjee.core.util.Attributes;

/**
 * Thymeleaf的HTML标签
 */
public class ThymeleafHtmlTag {

    private ITemplateContext context;
    private IProcessableElementTag origin;
    private Attributes attributes;

    public ThymeleafHtmlTag(ITemplateContext context, IProcessableElementTag origin) {
        this.context = context;
        this.origin = origin;
        this.attributes = new AttributeMap(origin.getAttributeMap());
    }

    public ITemplateContext getContext() {
        return context;
    }

    public IProcessableElementTag getOrigin() {
        return this.origin;
    }

    public <V> V getAttributeValue(String name) {
        String value = this.attributes.get(name);
        // 含有表达式，则进行表达式计算
        if (value.contains("${") && value.contains("}")) {
            AttributeName attributeName = AttributeNames.forName(TemplateMode.HTML, null, "value");
            IStandardExpression expression = EngineEventUtils.computeAttributeExpression(context,
                    this.origin, attributeName, value);
            return (V) expression.execute(context, StandardExpressionExecutionContext.NORMAL);
        }
        return (V) value;
    }

    public <V> V getAttributeValue(String name, V defaultValue) {
        return this.attributes.get(name, defaultValue);
    }

}
