package com.billion.async.processing.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xuliang
 * @since 2018/12/28
 */
public class AsyncThreadFactory implements ThreadFactory {

    public static final String THREAD_PREFIX = "async-processing";

    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    private final AtomicInteger currentThreadNumber = new AtomicInteger(1);

    private final ThreadGroup threadGroup;

    private final String namePrefix;

    private int priority = Thread.NORM_PRIORITY;

    private boolean isDaemon;

    public AsyncThreadFactory() {
        this(THREAD_PREFIX);
    }

    public AsyncThreadFactory(String prefix) {
        this(prefix, false);
    }

    public AsyncThreadFactory(String prefix, boolean isDaemon) {
        this(prefix, isDaemon, Thread.NORM_PRIORITY);
    }

    public AsyncThreadFactory(String prefix, boolean isDaemon, int priority) {
        SecurityManager s = System.getSecurityManager();
        this.threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
        this.isDaemon = isDaemon;
        this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(threadGroup, r, namePrefix + currentThreadNumber.getAndIncrement(), 0);
        thread.setDaemon(isDaemon);
        thread.setPriority(priority);
        return thread;
    }
}
