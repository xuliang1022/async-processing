package com.billion.async.processing;

import com.billion.async.processing.support.ZkAsyncHookRegister;
import com.billion.async.processing.zookeeper.ZookeeperClient;
import com.billion.async.processing.zookeeper.zkclient.ZkclientZookeeperClient;
import org.junit.Before;
import org.junit.Test;

/**
 * @author xuliang
 * @since 2019/05/13
 */
public class AsyncHookRegisterTest {

    private AsyncHookRegister register;
    private ZookeeperClient zkClient = new ZkclientZookeeperClient("localhost:2181");

    @Before
    public void setUp() {
        register = new ZkAsyncHookRegister(zkClient, 5000L, new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(" ============= i'm success !!! =================== ");
                synchronized (AsyncHookRegisterTest.class) {
                    AsyncHookRegisterTest.class.notify();
                }
            }
        }), new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(" ============ i'm timeout !!! =================== ");
            }
        }));
    }

    @Test
    public void registerTimeout() {
        register.registerHook("123456");
    }

    @Test
    public void registerSuccess() throws InterruptedException {
        register.registerHook("123456");
        Thread.sleep(5000L);
        zkClient.delete("123456");
        synchronized (AsyncHookRegisterTest.class) {
            AsyncHookRegisterTest.class.wait();
        }
        //System.out.println("hello game over");
    }

}
