package org.truenewx.tnxjee.web.controller.security.config.annotation;

import org.truenewx.tnxjee.core.Strings;

import java.lang.annotation.*;

/**
 * 配置允许匿名访问，一旦配置，同方法上的@{@link ConfigAuthority}将失效（如果有）
 *
 * @author jianglei
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigAnonymous {

    /**
     * @return 当前资源的访问链接的正则表达式。默认为空，框架自动采用Ant通配符形式
     */
    String regex() default Strings.EMPTY;

    /**
     * @return 是否只有内网可访问
     */
    boolean intranet() default false;
}
