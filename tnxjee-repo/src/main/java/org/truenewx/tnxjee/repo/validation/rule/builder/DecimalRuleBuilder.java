package org.truenewx.tnxjee.repo.validation.rule.builder;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.repo.validation.rule.DecimalRule;

/**
 * 数值规则的构建器
 *
 * @author jianglei
 */
@Component
public class DecimalRuleBuilder implements ValidationRuleBuilder<DecimalRule> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[] { Digits.class, Min.class, Max.class, DecimalMin.class,
                DecimalMax.class };
    }

    @Override
    public void update(Annotation annotation, DecimalRule rule) {
        Class<? extends Annotation> annoClass = annotation.annotationType();
        if (annoClass == Digits.class) {
            updateRule(rule, (Digits) annotation);
        } else if (annoClass == Min.class) {
            updateRule(rule, (Min) annotation);
        } else if (annoClass == Max.class) {
            updateRule(rule, (Max) annotation);
        } else if (annoClass == DecimalMin.class) {
            updateRule(rule, (DecimalMin) annotation);
        } else if (annoClass == DecimalMax.class) {
            updateRule(rule, (DecimalMax) annotation);
        }
    }

    private DecimalRule updateRule(DecimalRule rule, DecimalMax dm) {
        try {
            BigDecimal max = new BigDecimal(dm.value());
            if (max.compareTo(rule.getMax()) < 0) {
                rule.setMax(max);
                rule.setInclusiveMax(dm.inclusive());
            }
        } catch (NumberFormatException e) {
            this.logger.error(e.getMessage(), e);
        }
        return rule;
    }

    private DecimalRule updateRule(DecimalRule rule, DecimalMin dm) {
        try {
            BigDecimal min = new BigDecimal(dm.value());
            if (min.compareTo(rule.getMin()) > 0) {
                rule.setMin(min);
                rule.setInclusiveMin(dm.inclusive());
            }
        } catch (NumberFormatException e) {
            this.logger.error(e.getMessage(), e);
        }
        return rule;
    }

    private DecimalRule updateRule(DecimalRule rule, Max max) {
        BigDecimal maxValue = BigDecimal.valueOf(max.value());
        if (maxValue.compareTo(rule.getMax()) < 0) {
            rule.setMax(maxValue);
        }
        return rule;
    }

    private DecimalRule updateRule(DecimalRule rule, Min min) {
        BigDecimal minValue = BigDecimal.valueOf(min.value());
        if (minValue.compareTo(rule.getMin()) > 0) {
            rule.setMin(minValue);
        }
        return rule;
    }

    private DecimalRule updateRule(DecimalRule rule, Digits digits) {
        int scale = digits.fraction();
        if (scale < rule.getScale()) {
            rule.setScale(scale);
        }
        scale = rule.getScale();
        int precision = digits.integer() + (scale < 0 ? 0 : scale);
        if (precision < rule.getPrecision()) {
            rule.setPrecision(precision);
        }
        return rule;
    }

    @Override
    public DecimalRule create(Annotation annotation) {
        DecimalRule rule = new DecimalRule();
        update(annotation, rule);
        return rule;
    }

}
