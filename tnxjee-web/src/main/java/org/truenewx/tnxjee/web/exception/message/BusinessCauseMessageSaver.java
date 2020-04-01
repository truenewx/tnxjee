package org.truenewx.tnxjee.web.exception.message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;

/**
 * 业务异常作为异常原因的消息保存器
 */
@Component
public class BusinessCauseMessageSaver {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private BusinessExceptionMessageSaver businessExceptionMessageSaver;

    public void saveMessage(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        Throwable cause = e.getCause();
        if (cause instanceof ResolvableException) {
            ResolvableException re = (ResolvableException) cause;
            HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
            if (handlerMethod != null) {
                this.businessExceptionMessageSaver.saveMessage(request, response, handlerMethod, re);
            }
        }
    }

}