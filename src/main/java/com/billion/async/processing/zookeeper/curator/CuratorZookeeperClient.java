package com.billion.async.processing.zookeeper.curator;

import com.billion.async.processing.zookeeper.AbstractZookeeperClient;
import com.billion.async.processing.zookeeper.IChildListener;
import com.billion.async.processing.zookeeper.IDataListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;

/**
 * @author xuliang
 * @since 2018/12/26
 */
public class CuratorZookeeperClient extends AbstractZookeeperClient {

    private CuratorFramework client;

    public CuratorZookeeperClient(String address) {
        this(address, DEFAULT_TIMEOUT);
    }

    public CuratorZookeeperClient(String address, int timeout) {
        super(address, timeout);
        this.client = CuratorFrameworkFactory.builder()
                .connectString(address)
                .connectionTimeoutMs(timeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        this.client.start();
    }

    @Override
    protected void createEphemeral(String path) {
        path = validatePath(path);
        try {
            this.client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (NodeExistsException e) {
            //do nothing
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    protected void createPersistent(String path) {
        path = validatePath(path);
        try {
            this.client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        } catch (NodeExistsException e) {
            //do nothing
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void setData(String path, byte[] data) {
        path = validatePath(path);
        try {
            this.client.setData().forPath(path, data);
        } catch (NoNodeException e) {
            //do nothing
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String path) {
        path = validatePath(path);
        try {
            this.client.delete().forPath(path);
        } catch (NoNodeException e) {
            //do nothing
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public boolean exist(String path) {
        path = validatePath(path);
        try {
            Stat stat = this.client.checkExists().forPath(path);
            return stat != null;
        } catch (NoNodeException e) {
            return false;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void subscribe(String path, final IDataListener listener) {
        final String validPath = validatePath(path);
        try {
            NodeCache nodeCache = new NodeCache(this.client, validPath);
            nodeCache.start(true);
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() {
                    listener.onSubscribe(exist(validPath) ? IDataListener.NODE_CHANGED : IDataListener.NODE_DELETED);
                }
            });
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void subscribeChild(String path, final IChildListener listener) {
        final String validPath = validatePath(path);
        try {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(this.client, validPath, true);
            pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    listener.onSubscribe(client.getChildren().forPath(validPath));
                }
            });
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
