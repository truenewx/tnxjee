package org.truenewx.tnxjee.webmvc.security.web.authentication;

/**
 * 可接受登录进程处理器的
 *
 * @param <S> 登录成功处理器类型
 * @param <F> 登录失败处理器类型
 */
public interface LoginProcessingHandlerAcceptable<S, F>
        extends LoginSuccessHandlerAcceptable<S>, LoginFailureHandlerAcceptable<F> {
}
