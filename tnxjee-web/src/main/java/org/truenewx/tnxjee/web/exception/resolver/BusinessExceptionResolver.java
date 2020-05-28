package org.truenewx.tnxjee.web.exception.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.MultiException;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.service.exception.SingleException;
import org.truenewx.tnxjee.web.exception.message.ResolvableExceptionMessageSaver;

/**
 * 业务异常解决器
 */
public abstract class BusinessExceptionResolver extends AbstractHandlerExceptionResolver {

    @Autowired
    private ResolvableExceptionMessageSaver messageSaver;

    @Override
    protected final ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod && ex instanceof ResolvableException) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ResolvableException re = (ResolvableException) ex;
            if (supports(handlerMethod)) {
                this.messageSaver.saveMessage(request, response, handlerMethod, re);
                return getResult(request, response, handlerMethod);
            }
        }
        return null;
    }

    protected abstract boolean supports(HandlerMethod handlerMethod);

    protected abstract ModelAndView getResult(HttpServletRequest request,
            HttpServletResponse response, HandlerMethod handlerMethod);

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
            message.append(Strings.LEFT_BRACKET).append(be.getProperty()).append(Strings.RIGHT_BRACKET);
        }
        message.append(" ======");
        return message.toString();
    }

}
