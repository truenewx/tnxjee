package org.truenewx.tnxjee.web.controller.spring.web.servlet;

import org.springframework.web.servlet.HandlerExecutionChain;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求处理器源
 */
public interface RequestHandlerSource {

    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

}
