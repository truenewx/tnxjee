package org.truenewx.tnxjee.web.controller.exception.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.core.Model;

/**
 * 标注方法的可处理异常处理结果视图
 *
 * @author jianglei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleableExceptionResult {

    /**
     * 表示前一个页面的视图名称
     */
    public static final String PREV_VIEW = "prev:";

    /**
     * @return 结果视图名称，默认交给{@link BasicErrorController}处理，也可以使用{@link HandleableExceptionResult#PREV_VIEW}
     *         返回上一页面
     */
    String value() default Strings.EMPTY;

    /**
     * @return 异常处理完毕后要生成校验规则的模型类型集
     */
    Class<? extends Model>[] validate() default {};

    /**
     * @return 返回按钮的跳转地址
     */
    String back() default Strings.EMPTY;
}
