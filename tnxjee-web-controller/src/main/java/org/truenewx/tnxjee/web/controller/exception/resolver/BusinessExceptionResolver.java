package org.truenewx.tnxjee.web.controller.exception.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.service.api.exception.BusinessException;
import org.truenewx.tnxjee.service.api.exception.HandleableException;
import org.truenewx.tnxjee.service.api.exception.MultiException;
import org.truenewx.tnxjee.service.api.exception.SingleException;

/**
 * 业务异常解决器
 */
public class BusinessExceptionResolver extends AbstractHandlerExceptionResolver {

    public static final String ATTRIBUTE_ERRORS = "errors";

    @Autowired
    private MessageSource messageSource;

    @Override
    protected final ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod && ex instanceof HandleableException) {
            HandleableException he = (HandleableException) ex;
            List<HandledError> errors = toErrors(he, request.getLocale());
            if (errors.size() > 0) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                if (handlerMethod.getMethodAnnotation(ResponseBody.class) == null) {
                    request.setAttribute(ATTRIBUTE_ERRORS, errors);
                    return getErrorView(request, handlerMethod);
                } else {
                    try {
                        Map<String, Object> map = Map.of(ATTRIBUTE_ERRORS, errors);
                        response.getWriter().print(JsonUtil.toJson(map));
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return new ModelAndView();
                    } catch (IOException e) {
                        logException(e, request);
                    }
                }
            }
        }
        return null;
    }

    private List<HandledError> toErrors(HandleableException he, Locale locale) {
        List<HandledError> errors = new ArrayList<>();
        if (he instanceof BusinessException) { // 业务异常，转换错误消息
            BusinessException be = (BusinessException) he;
            String message = resolveExceptionMessage(be, locale);
            errors.add(HandledError.of(message, be));
        } else if (he instanceof MultiException) { // 业务异常集，转换错误消息
            MultiException me = (MultiException) he;
            for (SingleException se : me) {
                if (se instanceof BusinessException) {
                    BusinessException be = (BusinessException) se;
                    String message = resolveExceptionMessage(be, locale);
                    errors.add(HandledError.of(message, be));
                }
            }
        }
        return errors;
    }

    private String resolveExceptionMessage(BusinessException be, Locale locale) {
        if (be.isMessageLocalized()) {
            return be.getLocalizedMessage();
        }
        return this.messageSource.getMessage(be.getCode(), be.getArgs(), be.getCode(), locale);
    }

    @Override
    protected final void logException(Exception ex, HttpServletRequest request) {
        if (ex instanceof BusinessException) {
            logException((BusinessException) ex);
        } else if (ex instanceof MultiException) {
            MultiException me = (MultiException) ex;
            for (SingleException se : me) {
                if (se instanceof BusinessException) {
                    logException((BusinessException) se);
                }
            }
        } else {
            super.logException(ex, request);
        }
    }

    /**
     * 打印业务异常
     *
     * @param be 业务异常
     */
    private void logException(BusinessException be) {
        if (this.logger.isErrorEnabled()) {
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
            this.logger.error(message);
        }
    }

    /**
     * 获取展示错误的视图
     *
     * @param request       http请求
     * @param handlerMethod 处理方法
     * @return 展示错误的视图，返回null表示使用默认视图
     */
    protected ModelAndView getErrorView(HttpServletRequest request, HandlerMethod handlerMethod) {
        return null;
    }

}
