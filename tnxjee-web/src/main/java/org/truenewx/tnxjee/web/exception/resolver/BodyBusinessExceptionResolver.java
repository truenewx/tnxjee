package org.truenewx.tnxjee.web.exception.resolver;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.service.exception.NoAccessAuthority;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.util.SpringWebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
            HandlerMethod handlerMethod, ResolvableException re) {
        if (re instanceof NoAccessAuthority) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.setHeader("error", Boolean.TRUE.toString());
        }
        return new ModelAndView();
    }

}
