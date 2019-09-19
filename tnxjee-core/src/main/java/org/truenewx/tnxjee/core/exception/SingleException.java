package org.truenewx.tnxjee.core.exception;

import org.truenewx.tnxjee.core.Strings;

/**
 * 只包含一个异常信息的单异常<br/>
 * 仅作为标识，在进行异常处理时便于判断
 *
 * @author jianglei
 */
public abstract class SingleException extends HandleableException {

    private static final long serialVersionUID = -7817976044788876682L;

    protected String property;

    public SingleException() {
        super();
    }

    public SingleException(String message) {
        super(message);
    }

    public String getProperty() {
        return this.property;
    }

    public boolean matches(String property) {
        if (this.property == null) { // 未绑定属性，则指定匹配空属性
            return property == null;
        } else { // 已绑定属性，则匹配*、相等的属性
            return Strings.ASTERISK.equals(property) || this.property.equals(property);
        }
    }

}
