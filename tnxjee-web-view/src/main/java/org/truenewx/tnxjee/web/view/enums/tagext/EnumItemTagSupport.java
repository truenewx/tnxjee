package org.truenewx.tnxjee.web.view.enums.tagext;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.truenewx.tnxjee.core.enums.support.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.support.EnumItem;
import org.truenewx.tnxjee.core.enums.support.EnumType;
import org.truenewx.tnxjee.web.view.tagext.ItemTagSupport;

/**
 * 基于枚举选项的标签支持
 *
 * @author jianglei
 */
public abstract class EnumItemTagSupport extends ItemTagSupport {
    protected String type;
    protected String subtype;

    public void setType(String type) throws JspException {
        this.type = type;
    }

    public void setSubtype(String subtype) throws JspException {
        this.subtype = subtype;
    }

    @Override
    public void doTag() throws IOException {
        EnumDictResolver enumDictResolver = getBeanFromApplicationContext(EnumDictResolver.class);
        EnumType enumType = enumDictResolver.getEnumType(this.type, this.subtype, getLocale());
        if (enumType != null) {
            this.items = enumType.getItems();
            super.doTag();
        }
    }

    @Override
    protected String getItemValue(Object item) {
        return ((EnumItem) item).getKey();
    }

    @Override
    protected String getItemText(Object item) {
        return ((EnumItem) item).getCaption();
    }

}