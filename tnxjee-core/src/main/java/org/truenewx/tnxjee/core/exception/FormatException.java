package org.truenewx.tnxjee.core.exception;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.function.FuncHashCode;

/**
 * 格式异常，必须绑定属性
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FormatException extends SingleException {

    private static final long serialVersionUID = -7599751978935457915L;

    private Class<?> beanClass;
    private String violationMessage;

    public FormatException(Class<?> beanClass, String property, String violationMessage) {
        super(violationMessage);
        this.beanClass = beanClass;
        Assert.notNull(property, "property must be not null");
        this.property = property;
    }

    public FormatException(String property, String violationMessage) {
        this(null, property, violationMessage);
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public String getViolationMessage() {
        return this.violationMessage;
    }

    @Override
    public boolean matches(String property) {
        if (super.matches(property)) {
            return true;
        }
        String simplePropertyPath = ClassUtil.getSimplePropertyPath(this.beanClass, property);
        if (simplePropertyPath.equals(property)) {
            return true;
        }
        String fullPropertyPath = ClassUtil.getFullPropertyPath(this.beanClass, property);
        return fullPropertyPath.equals(property);
    }

    @Override
    public int hashCode() {
        Object[] array = { this.beanClass, this.violationMessage };
        return FuncHashCode.INSTANCE.apply(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FormatException other = (FormatException) obj;
        return BeanUtil.equals(this.beanClass, other.beanClass)
                && BeanUtil.equals(this.violationMessage, other.violationMessage);
    }
}
