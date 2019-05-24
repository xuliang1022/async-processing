package com.billion.async.processing;

/**
 * @author xuliang
 * @since 2019/1/2
 */
public interface HookRegister {

    /**
     * 注册异步回调，如果成功处理将会调用onCompletion的方法
     * 如果超时将会调用onTimeout的方法
     * 如果timeout <=0 的话，不会超时
     *
     * @param uniqueId 唯一标识
     */
    void registerHook(String uniqueId);

    /**
     * 超时时间
     *
     * @return 超时时间
     */
    long timeout();

}
