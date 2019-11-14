package org.truenewx.tnxjee.web.view.validation.generate;

import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.model.core.Model;

import java.util.Locale;

/**
 * 处理器校验规则填充器
 *
 * @author jianglei
 */
public interface HandlerValidationApplier {
    /**
     * 为多个模型类型生成校验规则，并填充至指定模型视图中
     *
     * @param mav
     *            模型视图
     * @param modelClasses
     *            模型类型集
     * @param locale
     *            区域
     */
    void applyValidation(ModelAndView mav, Class<? extends Model>[] modelClasses, Locale locale);

}
