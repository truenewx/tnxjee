package org.truenewx.tnxjee.service.spec.region;

import java.lang.annotation.*;

/**
 * 标注属性是否行政区划代码
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegionCode {
}
