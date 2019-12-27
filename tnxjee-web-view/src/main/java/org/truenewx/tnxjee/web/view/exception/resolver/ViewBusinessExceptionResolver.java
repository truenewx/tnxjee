package org.truenewx.tnxjee.web.view.exception.resolver;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.web.controller.exception.resolver.AbstractBusinessExceptionResolver;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;
import org.truenewx.tnxjee.web.controller.util.WebControllerUtil;
import org.truenewx.tnxjee.web.view.exception.annotation.ResolvableExceptionResult;
import org.truenewx.tnxjee.web.view.util.WebViewPropertyConstant;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;
import org.truenewx.tnxjee.web.view.validation.generate.HandlerValidationApplier;

/**
 * 业务异常处理至视图页面的解决器
 *
 * @author jianglei
 */
@Component
public class ViewBusinessExceptionResolver extends AbstractBusinessExceptionResolver implements EnvironmentAware {

    private String errorPath;

    @Override
    public void setEnvironment(Environment environment) {
        this.errorPath = environment.getProperty(WebViewPropertyConstant.ERROR_PATH_BUSINESS, "/error/business");
    }

    public String getErrorPath() {
        return this.errorPath;
    }

    /**
     * 处理器校验规则填充者
     */
    @Autowired(required = false)
    private HandlerValidationApplier handlerValidationApplier;

    public ViewBusinessExceptionResolver() {
        setOrder(getOrder() + 1); // 默认顺序提升1
    }

    @Override
    protected ModelAndView getResult(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod) {
        if (!SpringWebUtil.isResponseBody(handlerMethod)) {
            ModelAndView mav = new ModelAndView(this.errorPath);
            mav.addObject("ajaxRequest", WebViewUtil.isAjaxRequest(request));
            ResolvableExceptionResult her = handlerMethod.getMethodAnnotation(ResolvableExceptionResult.class);
            if (her != null) {
                String view = her.value();
                if (ResolvableExceptionResult.PREV_VIEW.equals(view)) {
                    view = WebViewUtil.getRelativePreviousUrl(request, false);
                }
                if (StringUtils.isEmpty(view)) { // 跳转到全局错误页面，则需设置返回按钮地址
                    mav.addObject("back", her.back());
                } else { // 非跳转到全局错误页面，则复制参数到属性集中，以便于可能的回填
                    mav.setViewName(view);
                    WebControllerUtil.copyParameters2Attributes(request);
                }
                // 生成校验规则的模型类集合
                Locale locale = SpringWebUtil.getLocale(request);
                if (this.handlerValidationApplier != null) {
                    this.handlerValidationApplier.applyValidation(mav, her.validate(), locale);
                }
            }
            return mav;
        }
        return null;
    }
}
