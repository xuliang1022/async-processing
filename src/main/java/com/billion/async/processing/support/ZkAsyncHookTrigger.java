package com.billion.async.processing.support;

import com.billion.async.processing.AsyncHookTrigger;
import com.billion.async.processing.zookeeper.ZookeeperClient;

/**
 * @author xuliang
 * @since 2018/12/28
 */
public class ZkAsyncHookTrigger implements AsyncHookTrigger {

    private ZookeeperClient client;

    public ZkAsyncHookTrigger(ZookeeperClient client) {
        this.client = client;
    }

    @Override
    public void trigger(String uniqueId) {
        if (client.exist(uniqueId)) {
            client.delete(uniqueId);
        }
    }
}
