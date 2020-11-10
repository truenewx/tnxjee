package org.truenewx.tnxjee.webmvc.feign;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Feign调用的多线程任务
 */
public abstract class FeignCallTask implements Runnable {

    private RequestAttributes requestAttributes;

    public FeignCallTask() {
        this.requestAttributes = RequestContextHolder.currentRequestAttributes();
    }

    @Override
    public final void run() {
        RequestContextHolder.setRequestAttributes(this.requestAttributes);
        doRun();
    }

    protected abstract void doRun();

}
