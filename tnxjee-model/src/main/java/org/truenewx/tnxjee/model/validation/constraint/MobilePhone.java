package org.truenewx.tnxjee.model.validation.constraint;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import org.truenewx.tnxjee.model.validation.constraint.validator.MobilePhoneValidator;

/**
 * 手机号码约束
 *
 * @author jianglei
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "1[0-9]{10}")
@Constraint(validatedBy = MobilePhoneValidator.class)
public @interface MobilePhone {

    String DEFAULT_MESSAGE = "{org.truenewx.tnxjee.model.validation.constraint.MobilePhone.message}";

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
