package org.truenewx.tnxjee.web.view.exception.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.web.exception.resolver.BusinessExceptionResolver;
import org.truenewx.tnxjee.web.util.SpringWebUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.web.view.exception.annotation.ResolvableExceptionResult;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;

/**
 * 业务异常处理至视图页面的解决器
 *
 * @author jianglei
 */
@Component
public class ViewBusinessExceptionResolver extends BusinessExceptionResolver
        implements EnvironmentAware {
    /**
     * 业务异常错误消息展示页面路径
     */
    public static final String ERROR_PATH_BUSINESS = "tnxjee.web.view.error.path.business";

    private String errorPath;

    @Override
    public void setEnvironment(Environment environment) {
        this.errorPath = environment.getProperty(ERROR_PATH_BUSINESS, "/error/business");
    }

    public String getErrorPath() {
        return this.errorPath;
    }

    public ViewBusinessExceptionResolver() {
        setOrder(getOrder() + 1); // 默认顺序提升1
    }

    @Override
    protected boolean supports(HandlerMethod handlerMethod) {
        return !SpringWebUtil.isResponseBody(handlerMethod);
    }

    @Override
    protected ModelAndView getResult(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod) {
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
                WebUtil.copyParameters2Attributes(request);
            }
        }
        return mav;
    }
}
