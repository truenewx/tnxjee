package org.truenewx.tnxjee.webmvc.security.web.access;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.webmvc.exception.message.ResolvableExceptionMessageSaver;
import org.truenewx.tnxjee.webmvc.util.RpcUtil;

/**
 * 访问拒绝后的业务异常处理器
 */
public class AccessDeniedBusinessExceptionHandler extends AccessDeniedHandlerImpl {

    @Autowired
    private ResolvableExceptionMessageSaver resolvableExceptionMessageSaver;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (RpcUtil.isInternalRpc(request)) { // 内部RPC调用直接返回403错误
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Throwable cause = accessDeniedException.getCause();
        if (cause instanceof ResolvableException) {
            this.resolvableExceptionMessageSaver.saveMessage(request, response, null, (ResolvableException) cause);
        }
        // 保存业务异常后，执行默认的处理机制
        super.handle(request, response, accessDeniedException);
    }
}
