package org.truenewx.tnxjee.core.beans;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 容器初始化后执行bean代理，为目标bean提供线程执行能力
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ContextInitializedBeanProxy implements Runnable, ContextInitializedBean {
    private Executor executor;
    private ApplicationContext context;
    private ContextInitializedBean target;
    private int runDelay;

    @Autowired(required = false)
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setTarget(ContextInitializedBean target) {
        Assert.isTrue(!(target instanceof ContextInitializedBeanProxy),
                "The target cann't be a ContextInitializedBeanProxy");
        this.target = target;
    }

    public ContextInitializedBean getTarget() {
        return this.target;
    }

    public void setRunDelay(int runDelay) {
        this.runDelay = runDelay;
    }

    @Override
    public void run() {
        if (this.runDelay > 0) {
            try {
                Thread.sleep(this.runDelay * 1000);
            } catch (InterruptedException e) {
                LogUtil.error(getClass(), e);
            }
        }
        try {
            if (this.target == null) {
                return;
            }
            this.target.afterInitialized(this.context);
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        if (this.target == null) {
            return;
        }
        this.context = context;
        if (this.executor != null) { // 优先使用线程池执行
            this.executor.execute(this);
        } else { // 如果没有线程池，则创建线程执行
            new Thread(this).start();
        }
    }

}
