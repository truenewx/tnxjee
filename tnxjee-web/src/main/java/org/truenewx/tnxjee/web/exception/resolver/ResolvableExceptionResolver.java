package org.truenewx.tnxjee.web.exception.resolver;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.service.exception.*;
import org.truenewx.tnxjee.web.exception.message.ResolvableExceptionMessageSaver;

/**
 * 可解决异常解决器
 */
public abstract class ResolvableExceptionResolver extends AbstractHandlerExceptionResolver {

    @Autowired
    private ResolvableExceptionMessageSaver messageSaver;

    @Override
    protected final ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod) {
            if (ex instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) ex;
                Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
                if (violations != null && violations.size() > 0) {
                    if (violations.size() == 1) {
                        ConstraintViolation<?> violation =
                                CollectionUtil.getFirst(violations, null);
                        ex = buildFormatException(violation, request);
                    } else {
                        MultiException me = new MultiException();
                        for (ConstraintViolation<?> violation : violations) {
                            me.add(buildFormatException(violation, request));
                        }
                        ex = me;
                    }
                }
            }
            if (ex instanceof ResolvableException) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                ResolvableException re = (ResolvableException) ex;
                if (supports(handlerMethod)) {
                    this.messageSaver.saveMessage(request, response, handlerMethod, re);
                    return getResult(request, response, handlerMethod, re);
                }
            }
        }
        return null;
    }

    private FormatException buildFormatException(ConstraintViolation<?> violation,
            HttpServletRequest request) {
        String code = violation.getMessage().replace("{", "").replace("}", "");
        String property = violation.getPropertyPath().toString();
        return new FormatException(code, violation.getRootBeanClass(), property);
    }

    protected abstract boolean supports(HandlerMethod handlerMethod);

    protected abstract ModelAndView getResult(HttpServletRequest request,
            HttpServletResponse response, HandlerMethod handlerMethod, ResolvableException re);

    @Override
    protected String buildLogMessage(Exception ex, HttpServletRequest request) {
        if (ex instanceof SingleException) {
            return buildLogMessage((SingleException) ex);
        } else if (ex instanceof MultiException) {
            MultiException me = (MultiException) ex;
            StringBuilder messasge = new StringBuilder();
            me.forEach(se -> {
                String singleMessage = buildLogMessage((SingleException) se);
                if (StringUtils.isNotBlank(singleMessage)) {
                    messasge.append(singleMessage).append("/n");
                }
            });
            return messasge.toString().trim();
        }
        return super.buildLogMessage(ex, request);
    }

    private String buildLogMessage(SingleException se) {
        if (se instanceof BusinessException) {
            buildLogMessage((BusinessException) se);
        } else if (se instanceof FormatException) {
            buildLogMessage((FormatException) se);
        }
        return null;
    }

    private String buildLogMessage(BusinessException be) {
        StringBuilder message = new StringBuilder("====== ").append(be.getCode());
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

    private String buildLogMessage(FormatException fe) {
        StringBuilder message = new StringBuilder("====== ");
        message.append(fe.getCode()).append(Strings.LEFT_BRACKET)
                .append(fe.getBeanClass().getName()).append(Strings.DOT).append(fe.getProperty())
                .append(Strings.RIGHT_BRACKET);
        message.append(" ======");
        return message.toString();
    }

}
