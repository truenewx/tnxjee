package org.truenewx.tnxjee.web.security.config.annotation;

import java.lang.annotation.*;

import org.truenewx.tnxjee.core.Strings;

/**
 * 配置权限限定。不设置任何属性意味着登录用户均可访问
 *
 * @author jianglei
 */
@Documented
@Target(ElementType.METHOD) // 为尽量避免错误的权限配置，只能在方法上使用而不能在类上使用，即使这样略显繁琐
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConfigAuthorities.class)
public @interface ConfigAuthority {

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
     * @return 所需许可
     */
    String permission() default Strings.EMPTY;

    /**
     * @return 是否只有内网可访问
     */
    boolean intranet() default false;

    /**
     * 动态分配的权限在运行期由用户配置，静态分配的权限在编码期由开发者分配给特定用户
     *
     * @return 是否动态分配，默认为true
     */
    boolean dynamic() default true;

}
