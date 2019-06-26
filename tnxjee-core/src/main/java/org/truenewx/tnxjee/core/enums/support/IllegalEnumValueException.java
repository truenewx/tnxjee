package org.truenewx.tnxjee.core.enums.support;

/**
 * 非法的枚举值异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class IllegalEnumValueException extends RuntimeException {

    private static final long serialVersionUID = -1748063571691163136L;

    public IllegalEnumValueException(Class<Enum<?>> enumClass, String value) {
        super("'" + value + "' is illegal value for " + enumClass.getName());
    }

}
