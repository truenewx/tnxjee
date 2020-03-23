package org.truenewx.tnxjee.web.http.annotation;

import java.lang.annotation.*;

/**
 * 结果过滤配置
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ResultFilters.class)
public @interface ResultFilter {
    /**
     * @return 需要过滤的类型，默认为当前结果类型
     */
    Class<?> type() default Object.class;

    /**
     * @return 包含的属性名称集，为空则不限定
     */
    String[] included() default {};

    /**
     * @return 排除的属性名称集，为空则不限定
     */
    String[] excluded() default {};

    /**
     * @return 附带caption字段的枚举字段名称集
     */
    String[] captionEnum() default {};
}
