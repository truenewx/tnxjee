package org.truenewx.tnxjee.web.exception.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import org.truenewx.tnxjee.service.exception.MultiException;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.service.exception.SingleException;
import org.truenewx.tnxjee.service.exception.message.SingleExceptionMessageResolver;
import org.truenewx.tnxjee.service.exception.model.MessagedError;
import org.truenewx.tnxjee.web.exception.model.MessagedErrorBody;
import org.truenewx.tnxjee.web.util.SpringWebUtil;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * 可解决异常消息保存器实现
 */
@Component
public class ResolvableExceptionMessageSaverImpl implements ResolvableExceptionMessageSaver {

    private static final String ERROR_CODE_FORMAT = "error.format";

    @Autowired
    private SingleExceptionMessageResolver resolver;

    @Override
    public void saveMessage(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, ResolvableException re) {
        List<MessagedError> errors = buildErrors(re, request.getLocale());
        if (errors.size() > 0) {
            if (WebUtil.isAjaxRequest(request) || SpringWebUtil.isResponseBody(handlerMethod)) {
                try {
                    MessagedErrorBody body = new MessagedErrorBody(errors);
                    String json = JsonUtil.toJson(body);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().print(json);
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            } else {
                request.setAttribute(ATTRIBUTE, errors);
            }
        }
    }

    private List<MessagedError> buildErrors(ResolvableException re, Locale locale) {
        List<MessagedError> errors = new ArrayList<>();
        if (re instanceof SingleException) {
            addErrors((SingleException) re, locale, errors);
        } else if (re instanceof MultiException) {
            for (SingleException se : (MultiException) re) {
                addErrors(se, locale, errors);
            }
        }
        return errors;
    }

    private void addErrors(SingleException se, Locale locale, List<MessagedError> errors) {
        String message = this.resolver.resolveMessage(se, locale);
        errors.add(new MessagedError(message, se.getCode(), se.getProperty()));
    }

}
