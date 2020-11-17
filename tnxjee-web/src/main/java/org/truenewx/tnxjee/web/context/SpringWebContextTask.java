package org.truenewx.tnxjee.web.context;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 传递Spring Web上下文的多线程任务
 */
public abstract class SpringWebContextTask implements Runnable {

    private RequestAttributes requestAttributes;

    public SpringWebContextTask() {
        this.requestAttributes = RequestContextHolder.currentRequestAttributes();
    }

    @Override
    public final void run() {
        RequestContextHolder.setRequestAttributes(this.requestAttributes);
        doRun();
    }

    protected abstract void doRun();

}
