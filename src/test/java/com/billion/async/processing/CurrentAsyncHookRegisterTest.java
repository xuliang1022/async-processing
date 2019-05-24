package com.billion.async.processing;

import com.billion.async.processing.support.ZkAsyncHookRegisterFactory;
import com.billion.async.processing.support.ZkAsyncHookTrigger;
import com.billion.async.processing.zookeeper.ZookeeperClient;
import com.billion.async.processing.zookeeper.curator.CuratorZookeeperClient;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xuliang
 * @since 2018/12/28
 */
public class CurrentAsyncHookRegisterTest {

    private static final long TIMEOUT = 5000L;

    private List<AsyncHookRegister> registerList;

    private ZookeeperClient client = new CuratorZookeeperClient("localhost:2181");

    private AtomicInteger successCounter = new AtomicInteger(0);

    private AtomicInteger timeoutCounter = new AtomicInteger(0);

    private CountDownLatch latch;

    private AsyncHookTrigger trigger;

    @Before
    public void setUp() {
        registerList = new ArrayList<>();
        AsyncHookRegisterFactory factory = new ZkAsyncHookRegisterFactory(client);
        for (int i = 0; i < 5000; i++) {
            registerList.add(factory.build(TIMEOUT, new Thread(new Runnable() {
                @Override
                public void run() {
                    int andIncrement = successCounter.incrementAndGet();
                    System.out.println("++++++++++ success counter: " + andIncrement);
                    latch.countDown();
                }
            }), new Thread(new Runnable() {
                @Override
                public void run() {
                    int andIncrement = timeoutCounter.incrementAndGet();
                    System.out.println("========== timeout counter: " + andIncrement);
                    latch.countDown();
                }
            })));
        }
        latch = new CountDownLatch(registerList.size());
        trigger = new ZkAsyncHookTrigger(client);
    }

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < registerList.size(); i++) {
            registerList.get(i).registerHook("/test/" + i);
            trigger.trigger("/test/" + i);
        }

        latch.await();
    }

}
