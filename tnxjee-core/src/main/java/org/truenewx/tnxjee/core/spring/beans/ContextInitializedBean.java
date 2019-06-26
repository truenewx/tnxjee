package org.truenewx.tnxjee.core.spring.beans;

import org.springframework.context.ApplicationContext;

/**
 * 在容器初始化完成后执行操作的bean
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface ContextInitializedBean {

    /**
     * 容器初始化完成后执行的动作
     *
     * @param context
     *            容器上下文
     */
    public void afterInitialized(ApplicationContext context) throws Exception;

}
