package org.truenewx.tnxjee.webmvc.security.config.annotation;

import java.lang.annotation.*;

import org.truenewx.tnxjee.core.Strings;

/**
 * {@link ConfigAuthority}的替代者，替代其中的许可限定为默认许可名称的限定，其它限定不变
 *
 * @author jianglei
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPermission {

    /**
     * @return 所需用户类型
     */
    String type() default Strings.EMPTY;

    /**
     * 仅当所需用户类型不为空时才有效
     *
     * @return 所需用户级别
     */
    String rank() default Strings.EMPTY;

    /**
     * @return 是否只有内网可访问
     */
    boolean intranet() default false;

}
