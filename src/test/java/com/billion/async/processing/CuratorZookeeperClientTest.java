package com.billion.async.processing;

import com.billion.async.processing.zookeeper.IChildListener;
import com.billion.async.processing.zookeeper.IDataListener;
import com.billion.async.processing.zookeeper.curator.CuratorZookeeperClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xuliang
 * @since 2019/05/13
 */
public class CuratorZookeeperClientTest {

    private CuratorZookeeperClient client;

    private final String path = "/xuliang/hello/11";

    @Before
    public void setUp() {
        client = new CuratorZookeeperClient("localhost:2181");
    }

    @Test
    public void create() {
        client.create(path, true);
        boolean exist = client.exist(path);
        Assert.assertTrue(exist);
    }

    @Test
    public void setData() {
        client.setData(path, "good boy".getBytes());
    }

    @Test
    public void delete() {
        client.delete(path);
    }

    @Test
    public void exist() {
        client.delete(path);
        Assert.assertFalse(client.exist(path));
        client.create(path, false);
        Assert.assertTrue(client.exist(path));
    }

    @Test
    public void timeout() {
        int timeout = client.getTimeout();
        System.out.println(timeout);
    }

    @Test
    public void address() {
        System.out.println(client.getAddress());
    }

    @Test
    public void subscribeNodeDelete() throws InterruptedException {
        client.create(path, false);
        client.subscribe(path, new IDataListener() {
            @Override
            public void onSubscribe(int state) {
                Assert.assertEquals(state, IDataListener.NODE_DELETED);
                System.out.println(" ====== current state:" + state + " ==============");
                synchronized (CuratorZookeeperClientTest.class) {
                    CuratorZookeeperClientTest.class.notify();
                }
            }
        });
        client.delete(path);
        synchronized (CuratorZookeeperClientTest.class) {
            CuratorZookeeperClientTest.class.wait();
        }
    }

    @Test
    public void subscribeNodeChanged() throws InterruptedException {
        client.create(path, false);
        final AtomicInteger counter = new AtomicInteger(0);
        client.subscribe(path, new IDataListener() {
            @Override
            public void onSubscribe(int state) {
                counter.getAndIncrement();
                System.out.println(" ====== current state:" + state + " =========== and counter: " + counter.get());
                synchronized (CuratorZookeeperClientTest.class) {
                    CuratorZookeeperClientTest.class.notify();
                }
            }
        });

        client.setData(path, "hello".getBytes());
        Thread.sleep(4000L);
        client.setData(path, "body".getBytes());
        Thread.sleep(4000L);
        client.delete(path);
        synchronized (CuratorZookeeperClientTest.class) {
            CuratorZookeeperClientTest.class.wait();
        }
    }


    @Test
    public void subscribeChild() throws InterruptedException {
        if (!client.exist(path)) {
            client.create(path, false);
        }
        client.subscribeChild(path, new IChildListener() {
            @Override
            public void onSubscribe(List<String> children) {
                System.out.println("=========== children: " + children.toString());
                synchronized (CuratorZookeeperClientTest.class) {
                    CuratorZookeeperClientTest.class.notify();
                }
            }
        });
        client.create(path + "/1", true);
        Thread.sleep(1000L);
        client.create(path + "/2", true);
        Thread.sleep(1000L);
        client.create(path + "/3", true);
        Thread.sleep(1000L);
        client.delete(path + "/1");
        Thread.sleep(1000L);
        client.delete(path + "/2");
        Thread.sleep(1000L);
        client.delete(path + "/3");
        synchronized (CuratorZookeeperClientTest.class) {
            CuratorZookeeperClientTest.class.wait();
        }
    }

}
