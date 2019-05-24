package com.billion.async.processing;

import com.billion.async.processing.monitor.RegisterMonitor;

/**
 * @author xuliang
 * @since 2019/05/13
 */
public abstract class AbstractAsyncHookRegister implements AsyncHookRegister {

    /** 超时时间 */
    protected final long timeout;

    /** 创建时间，用于做超时判断 */
    protected long createTime;

    /** 成功回调线程 */
    private final Thread successThread;

    /** 超时回调线程 */
    private final Thread timeoutThread;

    /**
     * 当前注册回调的状态
     *
     * {@link HookRegisterState#CREATED}   刚被创建状态，可以注册回调任务
     * {@link HookRegisterState#READY}     准备状态，不能注册回调任务
     * {@link HookRegisterState#COMPLETED} 成功线程被回调的状态
     * {@link HookRegisterState#CANCELED}  超时线程被回调的状态
     */
    private HookRegisterState currentState;

    public AbstractAsyncHookRegister(long timeout, Thread successThread, Thread timeoutThread) {
        this.timeout = timeout;
        this.successThread = successThread;
        this.timeoutThread = timeoutThread;
        this.currentState = HookRegisterState.CREATED;
    }

    @Override
    public Thread onSuccess() {
        return this.successThread;
    }

    @Override
    public Thread onTimeout() {
        return this.timeoutThread;
    }

    @Override
    public long timeout() {
        return this.timeout;
    }

    public long createTime() {
        return this.createTime;
    }

    @Override
    public void registerHook(String uniqueId) {
        if (onSuccess() == null) {
            throw new RuntimeException("successThread is null.");
        }

        if (timeout > 0 && onTimeout() == null) {
            throw new RuntimeException("timeoutThread is null.");
        }

        if (!getCurrentState().isInit()) {
            throw new RuntimeException("the register only can be used once");
        }

        setCurrentState(HookRegisterState.READY);
        RegisterMonitor.getInstance().register(uniqueId, this);
        this.createTime = System.currentTimeMillis();
        register(uniqueId);
    }

    protected abstract void register(String uniqueId);

    /**
     * 修改当前状态，线程安全
     *
     * @param state 即将修改的状态
     */
    protected synchronized void setCurrentState(HookRegisterState state) {
        this.currentState = state;
    }

    /**
     * 获取当前系统状态，线程安全
     *
     * @return 当前系统状态
     */
    public synchronized HookRegisterState getCurrentState() {
        return this.currentState;
    }

    /**
     * 成功被回调
     * 1.修改状态为完成
     * 2.调用成功线程
     *
     * @param uniqueId 唯一标识
     */
    protected synchronized void complete(String uniqueId) {
        setCurrentState(HookRegisterState.COMPLETED);
        onSuccess().start();
        finish(uniqueId);
    }

    /**
     * 超时被回调
     * 1.状态置为取消
     * 2.调用超时线程
     *
     * @param uniqueId 唯一标识
     */
    protected synchronized void cancel(String uniqueId) {
        setCurrentState(HookRegisterState.CANCELED);
        onTimeout().start();
        finish(uniqueId);
    }

    /**
     * 用于自定义了尾工作
     *
     * @param uniqueId 唯一标识
     */
    protected abstract void finish(String uniqueId);
}
