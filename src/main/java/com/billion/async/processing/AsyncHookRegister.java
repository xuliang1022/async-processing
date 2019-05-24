package com.billion.async.processing;

/**
 * 异步回调注册中心
 *
 * @author xuliang
 * @since 2018/12/24
 */
public interface AsyncHookRegister extends HookRegister {

    /**
     * 成功回调的线程
     *
     * @return {@link Thread}
     */
    Thread onSuccess();

    /**
     * 超时回调的线程
     *
     * @return {@link Thread}
     */
    Thread onTimeout();
}
