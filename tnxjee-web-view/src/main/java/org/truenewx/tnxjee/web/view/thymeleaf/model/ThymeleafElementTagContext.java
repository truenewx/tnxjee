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

import java.util.Map;

/**
 * Thymeleaf的元素标签上下文
 */
public class ThymeleafElementTagContext {

    private ITemplateContext context;
    private IProcessableElementTag tag;
    private Attributes attributes;

    public ThymeleafElementTagContext(ITemplateContext context, IProcessableElementTag tag) {
        this.context = context;
        this.tag = tag;
        this.attributes = new AttributeMap(tag.getAttributeMap());
    }

    public ITemplateContext getContext() {
        return context;
    }

    public IProcessableElementTag getTag() {
        return this.tag;
    }

    public <V> V getAttributeValue(String name) {
        String value = this.attributes.getAttribute(name);
        if (value != null) {
            // 含有表达式，则进行表达式计算
            if (value.contains("${") && value.contains("}")) {
                AttributeName attributeName = AttributeNames.forName(TemplateMode.HTML, null, "value");
                IStandardExpression expression = EngineEventUtils.computeAttributeExpression(context,
                        this.tag, attributeName, value);
                return (V) expression.execute(context, StandardExpressionExecutionContext.NORMAL);
            }
        }
        return (V) value;
    }

    public <V> V getAttributeValue(String name, V defaultValue) {
        return this.attributes.getAttribute(name, defaultValue);
    }

    public <V> Map<String, V> getAttributes(String... excludedKeys) {
        return (Map<String, V>) this.attributes.getAttributes(excludedKeys);
    }

}
