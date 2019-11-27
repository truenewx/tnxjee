package org.truenewx.tnxjee.web.view.exception.resolver;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.service.api.exception.BusinessException;
import org.truenewx.tnxjee.web.controller.exception.resolver.AbstractBusinessExceptionResolver;
import org.truenewx.tnxjee.web.controller.exception.resolver.ResolvedBusinessError;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;
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
public class ViewBusinessExceptionResolver extends AbstractBusinessExceptionResolver implements
        EnvironmentAware {

    public static final String ATTRIBUTE_ERRORS = BusinessException.class.getName() + ".errors";

    private String errorPath;

    @Override
    public void setEnvironment(Environment environment) {
        this.errorPath = environment.getProperty(WebViewPropertyConstant.ERROR_PATH_BUSINESS, "/error/business");
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
    protected ModelAndView doResolveErrors(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, List<ResolvedBusinessError> errors) {
        if (handlerMethod.getMethodAnnotation(ResponseBody.class) == null) {
            request.setAttribute(ATTRIBUTE_ERRORS, errors);
            ModelAndView mav = new ModelAndView(this.errorPath);
            mav.addObject("ajaxRequest", WebViewUtil.isAjaxRequest(request));
            ResolvableExceptionResult her = handlerMethod
                    .getMethodAnnotation(ResolvableExceptionResult.class);
            if (her != null) {
                String view = her.value();
                if (ResolvableExceptionResult.PREV_VIEW.equals(view)) {
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
        return null;
    }
}
