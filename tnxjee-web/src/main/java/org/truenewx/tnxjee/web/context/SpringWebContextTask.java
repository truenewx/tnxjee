package org.truenewx.tnxjee.web.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 传递Spring Web上下文的多线程任务
 */
public abstract class SpringWebContextTask implements Runnable {

    private RequestAttributes requestAttributes;
    private SecurityContext securityContext;

    public SpringWebContextTask() {
        this.requestAttributes = RequestContextHolder.currentRequestAttributes();
        this.securityContext = SecurityContextHolder.getContext();
    }

    @Override
    public final void run() {
        SecurityContextHolder.setContext(this.securityContext);
        RequestContextHolder.setRequestAttributes(this.requestAttributes);
        try {
            execute();
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
    }

    protected abstract void execute();

}
