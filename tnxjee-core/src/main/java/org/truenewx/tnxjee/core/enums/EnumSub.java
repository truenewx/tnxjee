package org.truenewx.tnxjee.core.enums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 枚举子集
 *
 * @author jianglei
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumSub {
    /**
     * @return 所属的枚举子集清单
     */
    String[] value();
}
