package org.truenewx.tnxjee.web.exception.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.web.util.SpringWebUtil;

/**
 * 业务异常处理至响应体中的解决器
 *
 * @author jianglei
 */
@Component
public class BodyBusinessExceptionResolver extends BusinessExceptionResolver {

    public BodyBusinessExceptionResolver() {
        setOrder(getOrder() + 2); // 默认顺序提升2
    }

    @Override
    protected boolean supports(HandlerMethod handlerMethod) {
        return SpringWebUtil.isResponseBody(handlerMethod);
    }

    @Override
    protected ModelAndView getResult(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 拒绝请求
        return new ModelAndView();
    }

}
