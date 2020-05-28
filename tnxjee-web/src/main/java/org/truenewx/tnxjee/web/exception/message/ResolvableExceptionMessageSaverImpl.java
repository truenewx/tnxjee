package org.truenewx.tnxjee.web.exception.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import org.truenewx.tnxjee.service.exception.model.BusinessError;
import org.truenewx.tnxjee.web.exception.model.BusinessErrorBody;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.web.util.SpringWebUtil;
import org.truenewx.tnxjee.web.util.WebUtil;

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
        List<BusinessError> errors = buildErrors(re, request.getLocale());
        if (errors.size() > 0) {
            if (isAjaxRequest(request, handlerMethod)) {
                try {
                    BusinessErrorBody body = new BusinessErrorBody(errors);
                    String json = JsonUtil.toJson(body);
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.getWriter().print(json);
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            } else {
                request.setAttribute(ATTRIBUTE, errors);
            }
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request, HandlerMethod handlerMethod) {
        if (handlerMethod != null) { // 优先根据处理方法进行判断，以利于性能
            return SpringWebUtil.isResponseBody(handlerMethod) || WebUtil.isAjaxRequest(request);
        }
        return this.handlerMethodMapping.isAjaxRequest(request);
    }

    private List<BusinessError> buildErrors(ResolvableException re, Locale locale) {
        List<BusinessError> errors = new ArrayList<>();
        if (re instanceof BusinessException) { // 业务异常，转换错误消息
            BusinessException be = (BusinessException) re;
            String message = this.resolver.resolveMessage(be, locale);
            errors.add(new BusinessError(message, be.getCode(), be.getProperty()));
        } else if (re instanceof MultiException) { // 业务异常集，转换错误消息
            MultiException me = (MultiException) re;
            for (SingleException se : me) {
                if (se instanceof BusinessException) {
                    BusinessException be = (BusinessException) se;
                    String message = this.resolver.resolveMessage(be, locale);
                    errors.add(new BusinessError(message, be.getCode(), be.getProperty()));
                }
            }
        }
        return errors;
    }

}
