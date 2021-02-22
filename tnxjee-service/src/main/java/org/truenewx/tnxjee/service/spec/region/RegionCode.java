package org.truenewx.tnxjee.service.spec.region;

import java.lang.annotation.*;

/**
 * 标注属性是行政区划代码
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegionCode {

    int captionLevel() default 1;

}
