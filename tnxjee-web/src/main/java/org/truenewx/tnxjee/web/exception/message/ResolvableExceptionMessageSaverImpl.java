package org.truenewx.tnxjee.web.exception.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.MultiException;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.service.exception.SingleException;
import org.truenewx.tnxjee.service.exception.message.BusinessExceptionMessageResolver;
import org.truenewx.tnxjee.web.exception.resolver.ResolvedBusinessError;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.web.util.SpringWebUtil;

/**
 * 可解决异常消息保存器实现
 */
@Component
public class ResolvableExceptionMessageSaverImpl implements ResolvableExceptionMessageSaver {

    @Autowired
    private BusinessExceptionMessageResolver resolver;
    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    @Override
    public void saveMessage(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, ResolvableException re) {
        List<ResolvedBusinessError> errors = buildErrors(re, request.getLocale());
        if (errors.size() > 0) {
            if (SpringWebUtil.isResponseBody(handlerMethod)) {
                try {
                    Map<String, Object> map = Map.of("errors", errors);
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    String json = JsonUtil.toJson(map);
                    response.getWriter().print(json);
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            } else {
                request.setAttribute(ATTRIBUTE, errors);
            }
        }
    }

    private List<ResolvedBusinessError> buildErrors(ResolvableException re, Locale locale) {
        List<ResolvedBusinessError> errors = new ArrayList<>();
        if (re instanceof BusinessException) { // 业务异常，转换错误消息
            BusinessException be = (BusinessException) re;
            String message = this.resolver.resolveMessage(be, locale);
            errors.add(ResolvedBusinessError.of(message, be));
        } else if (re instanceof MultiException) { // 业务异常集，转换错误消息
            MultiException me = (MultiException) re;
            for (SingleException se : me) {
                if (se instanceof BusinessException) {
                    BusinessException be = (BusinessException) se;
                    String message = this.resolver.resolveMessage(be, locale);
                    errors.add(ResolvedBusinessError.of(message, be));
                }
            }
        }
        return errors;
    }

    @Override
    public void saveMessage(HttpServletRequest request, HttpServletResponse response,
            ResolvableException re) throws Exception {
        HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
        if (handlerMethod != null) {
            saveMessage(request, response, handlerMethod, re);
        }
    }
}
