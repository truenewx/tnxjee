package org.truenewx.tnxjee.web.controller.exception.resolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;

/**
 * 业务异常处理至响应体中的解决器
 *
 * @author jianglei
 */
@Component
public class BodyBusinessExceptionResolver extends AbstractBusinessExceptionResolver {

    public static final String ATTRIBUTE_ERRORS = "errors";

    public BodyBusinessExceptionResolver() {
        setOrder(getOrder() + 2); // 默认顺序提升2
    }

    @Override
    protected ModelAndView doResolveErrors(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, List<ResolvedBusinessError> errors) {
        if (SpringWebUtil.isResponseBody(handlerMethod)) {
            try {
                Map<String, Object> map = Map.of(ATTRIBUTE_ERRORS, errors);
                response.setContentType("application/json;charset=" + Strings.ENCODING_UTF8);
                response.getWriter().print(JsonUtil.toJson(map));
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return new ModelAndView();
            } catch (IOException e) {
                logException(e, request);
            }
        }
        return null;
    }

}