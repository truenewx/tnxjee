package org.truenewx.tnxjee.web.view.thymeleaf.model;

import org.thymeleaf.model.IProcessableElementTag;
import org.truenewx.tnxjee.core.util.AttributeMap;
import org.truenewx.tnxjee.core.util.Attributes;

/**
 * 可处理的元素标签
 */
public class ProcessableElementTag {

    private IProcessableElementTag origin;
    private Attributes attributes;

    public ProcessableElementTag(IProcessableElementTag origin) {
        this.origin = origin;
        this.attributes = new AttributeMap(origin.getAttributeMap());
    }

    public IProcessableElementTag getOrigin() {
        return this.origin;
    }

    public String getAttributeValue(String name) {
        return this.attributes.get(name);
    }

    public <V> V getAttributeValue(String name, V defaultValue) {
        return this.attributes.get(name, defaultValue);
    }
}
