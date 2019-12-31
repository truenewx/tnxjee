package org.truenewx.tnxjee.web.controller.security.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.tnxjee.core.Strings;

/**
 * 配置权限限定
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.METHOD })
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
