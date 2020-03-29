package org.truenewx.tnxjee.web.view.validation.generate.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.web.util.SpringWebUtil;
import org.truenewx.tnxjee.web.view.validation.generate.HandlerValidationApplier;
import org.truenewx.tnxjee.web.view.validation.generate.annotation.ValidationGeneratable;

/**
 * 校验生成拦截器
 *
 * @author jianglei
 */
@Component("validationGenerateInterceptor")
public class ValidationGenerateInterceptor implements HandlerInterceptor {
    /**
     * 处理器校验规则填充者
     */
    private HandlerValidationApplier handlerValidationApplier;

    @Autowired
    public void setHandlerValidationApplier(HandlerValidationApplier handlerValidationApplier) {
        this.handlerValidationApplier = handlerValidationApplier;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            ValidationGeneratable vg = hm.getMethodAnnotation(ValidationGeneratable.class);
            if (vg != null) {
                Locale locale = SpringWebUtil.getLocale(request);
                this.handlerValidationApplier.applyValidation(modelAndView, vg.value(), locale);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
    }

}
