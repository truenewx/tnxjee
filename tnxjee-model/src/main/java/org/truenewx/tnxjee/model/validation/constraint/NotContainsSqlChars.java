package org.truenewx.tnxjee.model.validation.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.truenewx.tnxjee.model.validation.constraint.validator.NotContainsSqlCharsValidator;

/**
 * 不能包含SQL字符，这些特殊字符可能影响数据查询的准确性
 *
 * @author jianglei
 * 
 */
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
@NotContains({ "?", "%" })
@ReportAsSingleViolation
@Constraint(validatedBy = NotContainsSqlCharsValidator.class)
public @interface NotContainsSqlChars {

    String message() default NotContains.DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
