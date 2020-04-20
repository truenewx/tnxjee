package org.truenewx.tnxjee.web.security.config.annotation;

import java.lang.annotation.*;

import org.truenewx.tnxjee.core.Strings;

/**
 * 配置权限限定。不设置任何属性意味着登录用户均可访问
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConfigAuthorities.class)
public @interface ConfigAuthority {

    /**
     * @return 所需角色
     */
    String role() default Strings.EMPTY;

    /**
     * @return 所需许可
     */
    String permission() default Strings.EMPTY;

    /**
     * @return 是否只有内网可访问
     */
    boolean intranet() default false;
}
