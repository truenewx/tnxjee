package org.truenewx.tnxjee.web.controller.exception.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.api.exception.BusinessException;
import org.truenewx.tnxjee.service.api.exception.MultiException;
import org.truenewx.tnxjee.service.api.exception.ResolvableException;
import org.truenewx.tnxjee.service.api.exception.SingleException;
import org.truenewx.tnxjee.service.api.exception.message.BusinessExceptionMessageResolver;

/**
 * 业务异常解决器
 */
public abstract class AbstractBusinessExceptionResolver extends AbstractHandlerExceptionResolver {

    @Autowired
    private BusinessExceptionMessageResolver messageResolver;

    @Override
    protected final ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod && ex instanceof ResolvableException) {
            ResolvableException he = (ResolvableException) ex;
            List<ResolvedBusinessError> errors = buildErrors(he, request.getLocale());
            if (errors.size() > 0) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                return doResolveErrors(request, response, handlerMethod, errors);
            }
        }
        return null;
    }

    private List<ResolvedBusinessError> buildErrors(ResolvableException he, Locale locale) {
        List<ResolvedBusinessError> errors = new ArrayList<>();
        if (he instanceof BusinessException) { // 业务异常，转换错误消息
            BusinessException be = (BusinessException) he;
            String message = this.messageResolver.resolveMessage(be, locale);
            errors.add(ResolvedBusinessError.of(message, be));
        } else if (he instanceof MultiException) { // 业务异常集，转换错误消息
            MultiException me = (MultiException) he;
            for (SingleException se : me) {
                if (se instanceof BusinessException) {
                    BusinessException be = (BusinessException) se;
                    String message = this.messageResolver.resolveMessage(be, locale);
                    errors.add(ResolvedBusinessError.of(message, be));
                }
            }
        }
        return errors;
    }

    @Override
    protected String buildLogMessage(Exception ex, HttpServletRequest request) {
        if (ex instanceof BusinessException) {
            return buildLogMessage((BusinessException) ex);
        } else if (ex instanceof MultiException) {
            MultiException me = (MultiException) ex;
            for (SingleException se : me) {
                if (se instanceof BusinessException) {
                    return buildLogMessage((BusinessException) se);
                }
            }
        }
        return super.buildLogMessage(ex, request);
    }

    private String buildLogMessage(BusinessException be) {
        StringBuffer message = new StringBuffer("====== ").append(be.getCode());
        String args = StringUtils.join(be.getArgs(), Strings.COMMA);
        if (args.length() > 0) {
            message.append(Strings.COLON).append(args);
        }
        if (be.isBoundProperty()) {
            message.append(Strings.LEFT_BRACKET).append(be.getProperty())
                    .append(Strings.RIGHT_BRACKET);
        }
        message.append(" ======");
        return message.toString();
    }

    protected abstract ModelAndView doResolveErrors(HttpServletRequest request,
            HttpServletResponse response, HandlerMethod handlerMethod,
            List<ResolvedBusinessError> errors);

}
