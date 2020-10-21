package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.util.function.Consumer;

/**
 * 可接受登录成功处理器的
 *
 * @param <T> 登录成功处理器类型
 */
public interface LoginSuccessHandlerAcceptable<T> {

    void acceptSuccessHandler(Consumer<T> consumer);

}
