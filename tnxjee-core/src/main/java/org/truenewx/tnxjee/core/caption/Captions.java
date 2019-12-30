package org.truenewx.tnxjee.core.caption;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明注解集
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD,
        ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Captions {

    Caption[] value() default {};

}
