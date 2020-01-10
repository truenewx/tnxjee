package org.truenewx.tnxjee.web.controller.exception.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.api.exception.BusinessException;
import org.truenewx.tnxjee.service.api.exception.MultiException;
import org.truenewx.tnxjee.service.api.exception.ResolvableException;
import org.truenewx.tnxjee.service.api.exception.SingleException;
import org.truenewx.tnxjee.service.api.exception.message.BusinessExceptionMessageResolver;
import org.truenewx.tnxjee.web.controller.exception.resolver.ResolvedBusinessError;
import org.truenewx.tnxjee.web.controller.util.SpringWebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 业务异常消息保存到响应体中的保存器
 */
@Component
public class BusinessExceptionMessageSaverImpl implements BusinessExceptionMessageSaver {

    @Autowired
    private BusinessExceptionMessageResolver resolver;

    @Override
    public void saveMessage(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,
            ResolvableException re) {
        List<ResolvedBusinessError> errors = buildErrors(re, request.getLocale());
        if (errors.size() > 0) {
            if (SpringWebUtil.isResponseBody(handlerMethod)) {
                try {
                    Map<String, Object> map = Map.of("errors", errors);
                    response.setContentType("application/json;charset=" + Strings.ENCODING_UTF8);
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

    protected final List<ResolvedBusinessError> buildErrors(ResolvableException re, Locale locale) {
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

}
