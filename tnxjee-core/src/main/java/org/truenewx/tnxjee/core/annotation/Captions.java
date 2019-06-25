package org.truenewx.tnxjee.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明注解，用于注释、说明、解释
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD,
        ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Captions {

    Caption[] value() default {};

}
