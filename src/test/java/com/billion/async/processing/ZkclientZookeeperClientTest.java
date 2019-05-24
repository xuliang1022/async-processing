package com.billion.async.processing;

import com.billion.async.processing.zookeeper.IChildListener;
import com.billion.async.processing.zookeeper.IDataListener;
import com.billion.async.processing.zookeeper.zkclient.ZkclientZookeeperClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author xuliang
 * @since 2019/05/13
 */
public class ZkclientZookeeperClientTest {

    private ZkclientZookeeperClient client;

    private final String path = "/xuliang/hello/11";

    @Before
    public void setUp() {
        client = new ZkclientZookeeperClient("localhost:2181");
    }

    @Test
    public void create() {
        client.create(path, false);
        boolean exist = client.exist(path);
        Assert.assertTrue(exist);
    }

    @Test
    public void setData() {
        client.create(path, false);
        client.setData(path, "hello. goodboy".getBytes());
    }

    @Test
    public void delete() {
        client.delete(path);
        boolean exist = client.exist(path);
        Assert.assertFalse(exist);
    }


    @Test
    public void exist() {
        client.create(path, true);
        boolean exist = client.exist(path);
        Assert.assertTrue(exist);
    }

    @Test
    public void timeout() {
        int timeout = client.getTimeout();
        System.out.println(timeout);
    }

    @Test
    public void address() {
        String address = client.getAddress();
        System.out.println(address);
    }

    @Test
    public void subscribeNodeDelete() {
        client.create(path, false);
        client.subscribe(path, new IDataListener() {
            @Override
            public void onSubscribe(int state) {
                long id = Thread.currentThread().getId();
                System.out.println("============= inner current thread: " + id + " =======================");
                System.out.println("============ " + state + " ============");
                Assert.assertEquals(state, IDataListener.NODE_DELETED);
                synchronized (ZkclientZookeeperClientTest.class) {
                    ZkclientZookeeperClientTest.class.notify();
                }
            }
        });
        client.delete(path);
        try {
            long id = Thread.currentThread().getId();
            System.out.println("============= outer current thread: " + id + " =======================");
            synchronized (ZkclientZookeeperClientTest.class) {
                ZkclientZookeeperClientTest.class.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void subscribeNodeChanged() throws InterruptedException {
        client.delete(path);
        client.create(path, false);
        client.subscribe(path, new IDataListener() {
            @Override
            public void onSubscribe(int state) {
                System.out.println("============= current state ==========");
                synchronized (ZkclientZookeeperClientTest.class) {
                    ZkclientZookeeperClientTest.class.notify();
                }
            }
        });
        client.setData(path, "hello. good boy".getBytes());
        Thread.sleep(5000L);
        client.setData(path, "hello, boy".getBytes());
        synchronized (ZkclientZookeeperClientTest.class) {
            ZkclientZookeeperClientTest.class.wait();
        }
    }

    @Test
    public void subscribeChild() throws InterruptedException {
        client.delete(path);
        String currentPath = "/xuliang/hello";
        client.create(currentPath, false);
        client.subscribeChild(currentPath, new IChildListener() {
            @Override
            public void onSubscribe(List<String> children) {
                System.out.println(" ========= current children:" + children.toString());
                synchronized (ZkclientZookeeperClientTest.class) {
                    ZkclientZookeeperClientTest.class.notify();
                }
            }
        });
        client.create(path, true);
        Thread.sleep(3000L);
        client.delete(path);
        Thread.sleep(3000L);
        client.create(path, true);
        Thread.sleep(3000L);
        client.create(currentPath  + "/234", true);
        synchronized (ZkclientZookeeperClientTest.class) {
            ZkclientZookeeperClientTest.class.wait();
        }
    }


}
