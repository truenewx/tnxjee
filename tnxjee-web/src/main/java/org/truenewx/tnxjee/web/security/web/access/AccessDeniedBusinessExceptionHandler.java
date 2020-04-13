package org.truenewx.tnxjee.web.security.web.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.truenewx.tnxjee.web.exception.message.BusinessCauseMessageSaver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 访问拒绝后的业务异常处理器
 */
public class AccessDeniedBusinessExceptionHandler extends AccessDeniedHandlerImpl {

    @Autowired
    private BusinessCauseMessageSaver businessCauseMessageSaver;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        try {
            this.businessCauseMessageSaver.saveMessage(request, response, accessDeniedException);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        // 保存业务异常后，执行默认的处理机制
        super.handle(request, response, accessDeniedException);
    }
}
