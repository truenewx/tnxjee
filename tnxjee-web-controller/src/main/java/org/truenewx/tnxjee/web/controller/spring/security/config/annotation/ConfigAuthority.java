package org.truenewx.tnxjee.web.controller.spring.security.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.tnxjee.core.Strings;

/**
 * 配置权限
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConfigAuthorities.class)
public @interface ConfigAuthority {
    /**
     * @return 是否匿名即可访问，一旦设为true，则角色和许可设置将被忽略
     */
    boolean anonymous() default false;

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
