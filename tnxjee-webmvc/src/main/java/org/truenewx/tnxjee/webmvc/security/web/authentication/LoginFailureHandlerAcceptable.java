package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.util.function.Consumer;

/**
 * 可接受登录失败处理器的
 *
 * @param <T> 登录失败处理器类型
 */
public interface LoginFailureHandlerAcceptable<T> {

    void acceptFailureHandler(Consumer<T> consumer);

}
