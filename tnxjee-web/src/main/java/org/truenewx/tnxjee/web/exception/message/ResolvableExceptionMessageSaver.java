package org.truenewx.tnxjee.web.exception.message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.service.exception.ResolvableException;

/**
 * 可解决异常消息保存器
 */
public interface ResolvableExceptionMessageSaver {

    String ATTRIBUTE = ResolvableException.class.getName() + ".errors";

    /**
     * 保存指定可解决异常中的异常消息到请求或响应中，以便后续处理
     *
     * @param request       HTTP请求
     * @param response      HTTP响应
     * @param handlerMethod 处理方法
     * @param re            可解决异常
     */
    void saveMessage(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, ResolvableException re);

    /**
     * 保存指定可解决异常中的异常消息到请求或响应中，以便后续处理
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param re       可解决异常
     */
    void saveMessage(HttpServletRequest request, HttpServletResponse response,
            ResolvableException re) throws Exception;

}
