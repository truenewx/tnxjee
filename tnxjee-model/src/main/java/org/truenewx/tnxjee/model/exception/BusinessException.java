package org.truenewx.tnxjee.model.exception;

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

    public BusinessException(String code, String message, Object... args) {
        super(message);
        this.code = code;
        this.args = args;
    }

    public BusinessException(String code, Object... args) {
        this(code, code, args);
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getArgs() {
        return this.args;
    }

    /**
     * 判断异常错误消息是否已经过本地化处理，经过本地化处理后方可呈现给用户查看
     *
     * @return 异常错误消息是否已经过本地化处理
     */
    public boolean isMessageLocalized() {
        return !this.code.equals(getMessage());
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
