package org.truenewx.tnxjee.core.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认线程池执行器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultThreadPoolExecutor extends ThreadPoolExecutor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private int logPerTaskCount = 10;

    public DefaultThreadPoolExecutor(int corePoolSize, int maxPoolSize) {
        super(corePoolSize, maxPoolSize, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        setRejectedExecutionHandler(new CallerRunsPolicy());
        // 允许核心线程空闲超时退出，超时时间默认为10秒
        allowCoreThreadTimeOut(true);
        setKeepAliveTime(10, TimeUnit.SECONDS);
    }

    public DefaultThreadPoolExecutor(int corePoolSize) {
        this(corePoolSize, corePoolSize * 10);
    }

    public void setLogPerTaskCount(int logPerTaskCount) {
        if (logPerTaskCount > 0) {
            this.logPerTaskCount = logPerTaskCount;
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        long completedTaskCount = getCompletedTaskCount();
        if (completedTaskCount % this.logPerTaskCount == 0) {
            this.logger
                    .info("Thread pool:size=" + getPoolSize() + " largest=" + getLargestPoolSize()
                            + " active=" + getActiveCount() + " completed=" + completedTaskCount);
        }
    }

}
