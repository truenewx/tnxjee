package org.truenewx.tnxjee.model.annotation;

import java.lang.annotation.*;

/**
 * 标注属性在RPC传递时忽略
 */
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcJson {
}
