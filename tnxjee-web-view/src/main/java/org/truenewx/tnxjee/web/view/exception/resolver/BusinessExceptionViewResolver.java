package org.truenewx.tnxjee.web.view.exception.resolver;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.web.controller.exception.resolver.BusinessExceptionResolver;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;
import org.truenewx.tnxjee.web.view.exception.annotation.HandleableExceptionResult;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;
import org.truenewx.tnxjee.web.view.validation.generate.HandlerValidationApplier;

/**
 * 业务异常的视图层解决器
 *
 * @author jianglei
 */
public class BusinessExceptionViewResolver extends BusinessExceptionResolver {
    @Autowired
    private ErrorController errorController;

    /**
     * 处理器校验规则填充者
     */
    @Autowired(required = false)
    private HandlerValidationApplier handlerValidationApplier;

    @Override
    protected ModelAndView getErrorView(HttpServletRequest request, HandlerMethod handlerMethod) {
        ModelAndView mav = new ModelAndView(this.errorController.getErrorPath());
        mav.addObject("ajaxRequest", WebViewUtil.isAjaxRequest(request));
        HandleableExceptionResult her = handlerMethod
                .getMethodAnnotation(HandleableExceptionResult.class);
        if (her != null) {
            String view = her.value();
            if (HandleableExceptionResult.PREV_VIEW.equals(view)) {
                view = WebViewUtil.getRelativePreviousUrl(request, false);
            }
            if (StringUtils.isEmpty(view)) { // 跳转到全局错误页面，则需设置返回按钮地址
                mav.addObject("back", her.back());
            } else { // 非跳转到全局错误页面，则复制参数到属性集中，以便于可能的回填
                mav.setViewName(view);
                WebViewUtil.copyParameters2Attributes(request);
            }
            // 生成校验规则的模型类集合
            Locale locale = SpringWebUtil.getLocale(request);
            if (this.handlerValidationApplier != null) {
                this.handlerValidationApplier.applyValidation(mav, her.validate(), locale);
            }
        }
        return mav;
    }
}
