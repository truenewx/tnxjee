package org.truenewx.tnxjee.model.validation.constraint.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;

import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.validation.constraint.IdCardNo;

/**
 * 身份证号码校验器
 */
public class IdCardNoValidator implements ConstraintValidator<IdCardNo, CharSequence> {

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Pattern pattern = IdCardNo.class.getAnnotation(Pattern.class);
        return StringUtil.regexMatch(value.toString(), pattern.regexp());
    }

}
