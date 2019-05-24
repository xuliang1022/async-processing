package com.billion.async.processing;

/**
 * 因为AsyncHookRegister实例化后不能复用
 * 因此增加工厂模式对ZkAsyncHookRegister进行实例化构建
 * 同时因为能够更好的结合spring，使用工厂模式确保每次实例化的对象都是刚被new的对象
 *
 * @author xuliang
 * @since 2018/12/28
 */
public interface AsyncHookRegisterFactory {

    AsyncHookRegister build(long timeout, Thread successThread, Thread timeoutThread);

}
