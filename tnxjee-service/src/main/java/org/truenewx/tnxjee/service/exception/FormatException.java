package org.truenewx.tnxjee.service.exception;

import java.util.Objects;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.service.exception.model.MessagedError;

/**
 * 格式异常，必须绑定属性
 *
 * @author jianglei
 */
public class FormatException extends SingleException {

    private static final long serialVersionUID = -7599751978935457915L;

    private Class<?> beanClass;

    public FormatException(String code, Class<?> beanClass, String property) {
        super(code);
        this.beanClass = beanClass;
        this.property = property;
    }

    public FormatException(MessagedError error) {
        super(error);
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
        return super.hashCode() + Objects.hash(this.beanClass);
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
        return super.equals(other) && Objects.equals(this.beanClass, other.beanClass);
    }

}
