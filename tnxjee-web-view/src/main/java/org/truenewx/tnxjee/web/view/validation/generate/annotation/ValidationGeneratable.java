package org.truenewx.tnxjee.web.view.validation.generate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.tnxjee.model.Model;

/**
 * 结果页面中可生成校验
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ValidationGeneratable {
    /**
     * 需生成校验的模型类型集
     *
     * @return 需生成校验的模型类型集
     */
    Class<? extends Model>[] value();
}
