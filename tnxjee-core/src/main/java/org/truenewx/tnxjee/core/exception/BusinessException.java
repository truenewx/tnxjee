package org.truenewx.tnxjee.core.exception;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.function.FuncHashCode;

/**
 * 业务异常，可以绑定属性，默认未绑定属性
 *
 * @author jianglei
 */
public class BusinessException extends SingleException {

    private static final long serialVersionUID = 3188183601455385859L;

    private String code;
    private Object[] args;

    public BusinessException(String code, Object... args) {
        super(code);
        this.code = code;
        this.args = args == null ? new Object[0] : args;
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getArgs() {
        return this.args;
    }

    /**
     * 与指定属性绑定
     *
     * @param property 绑定的属性
     * @return 当前异常对象自身
     */
    public BusinessException bind(String property) {
        this.property = property;
        return this;
    }

    /**
     * 判断是否已绑定属性
     *
     * @return 是否已绑定属性
     */
    public boolean isBoundProperty() {
        return StringUtils.isNotBlank(this.property);
    }

    @Override
    public int hashCode() {
        Object[] array = { this.code, this.args };
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
        BusinessException other = (BusinessException) obj;
        return BeanUtil.equals(this.code, other.code) && BeanUtil.equals(this.args, other.args);
    }

}
