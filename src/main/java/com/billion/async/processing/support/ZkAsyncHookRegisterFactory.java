package com.billion.async.processing.support;

import com.billion.async.processing.AsyncHookRegisterFactory;
import com.billion.async.processing.zookeeper.ZookeeperClient;

/**
 * 因为ZkAsyncHookRegister实例化后不能复用
 * 因此增加工厂模式对ZkAsyncHookRegister进行实例化构建
 * 同时因为能够更好的结合spring，使用工厂模式确保每次实例化的对象都是刚被new的对象
 * 同时通过此方法能更好的持有zkclient
 *
 * @author xuliang
 * @since 2018/12/28
 */
public class ZkAsyncHookRegisterFactory implements AsyncHookRegisterFactory {

    /** zk客服端实例 */
    private ZookeeperClient client;

    public ZkAsyncHookRegisterFactory() {
    }

    public ZkAsyncHookRegisterFactory(ZookeeperClient client) {
        this.client = client;
    }

    /**
     * 构建注册中心
     *
     * @return {@link ZkAsyncHookRegister}
     */
    @Override
    public ZkAsyncHookRegister build(long timeout, Thread successThread, Thread timeoutThread) {
        return new ZkAsyncHookRegister(client, timeout, successThread, timeoutThread);
    }

    public void setClient(ZookeeperClient client) {
        this.client = client;
    }
}
