package com.billion.async.processing.executor;

import java.util.concurrent.*;

/**
 * @author xuliang
 * @since 2018/12/28
 */
public class AsyncExecutorService extends ThreadPoolExecutor {

    private static final int DEFAULT_MIN_SIZE = 10;
    private static final int DEFAULT_MAX_SIZE = 200;
    private static final int DEFAULT_IDLE_TIME = 60 * 1000;

    public AsyncExecutorService() {
        this(DEFAULT_MIN_SIZE, DEFAULT_MAX_SIZE);
    }

    public AsyncExecutorService(int coreThread, int maxThreads) {
        this(coreThread, maxThreads, DEFAULT_IDLE_TIME, TimeUnit.MILLISECONDS);
    }

    public AsyncExecutorService(int coreThread, int maxThreads, long keepAliveTime, TimeUnit unit) {
        this(coreThread, maxThreads, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>());
    }

    public AsyncExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                BlockingQueue<Runnable> workQueue) {
        super(corePoolSize,maximumPoolSize, keepAliveTime, unit, workQueue, new AsyncThreadFactory(),
                new CallerRunsPolicy());
    }

    public static final AsyncExecutorService INSTANCE = new AsyncExecutorService();

    public static AsyncExecutorService getInstance() {
        return INSTANCE;
    }
}
