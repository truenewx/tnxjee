package org.truenewx.tnxjee.service.exception;

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

    public FormatException(Class<?> beanClass, String property, String message) {
        super(message);
        this.beanClass = beanClass;
        Assert.notNull(property, "property must be not null");
        this.property = property;
    }

    public FormatException(String property, String message) {
        this(null, property, message);
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
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
        Object[] array = { this.beanClass, this.property, getMessage() };
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
                && BeanUtil.equals(this.property, other.property)
                && BeanUtil.equals(getMessage(), other.getMessage());
    }
}
