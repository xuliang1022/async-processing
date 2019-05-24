package com.billion.async.processing.zookeeper.zkclient;

import com.billion.async.processing.zookeeper.AbstractZookeeperClient;
import com.billion.async.processing.zookeeper.IChildListener;
import com.billion.async.processing.zookeeper.IDataListener;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.List;

/**
 * @author xuliang
 * @since 2018/12/26
 */
public class ZkclientZookeeperClient extends AbstractZookeeperClient {

    private ZkClient zkClient;

    public ZkclientZookeeperClient(String address) {
        this(address, DEFAULT_TIMEOUT);
    }

    public ZkclientZookeeperClient(String address, int timeout) {
        super(address, timeout);
        this.zkClient = new ZkClient(address, timeout);
        this.zkClient.setZkSerializer(new StringSerializer());
    }

    @Override
    protected void createEphemeral(String path) {
        path = validatePath(path);
        try {
            this.zkClient.createEphemeral(path);
        } catch (ZkNodeExistsException e) {
            //do nothing
        }
    }

    @Override
    protected void createPersistent(String path) {
        path = validatePath(path);
        try {
            this.zkClient.createPersistent(path);
        } catch (ZkNodeExistsException e) {
            //do nothing
        }
    }

    @Override
    public void setData(String path, byte[] data) {
        path = validatePath(path);
        try {
            this.zkClient.writeData(path, data);
        } catch (ZkNoNodeException e) {
            //do nothing
        }
    }

    @Override
    public void delete(String path) {
        path = validatePath(path);
        try {
            this.zkClient.delete(path);
        } catch (ZkNoNodeException e) {
            //do nothing
        }
    }

    @Override
    public boolean exist(String path) {
        path = validatePath(path);
        try {
            return this.zkClient.exists(path);
        } catch (ZkNoNodeException e) {
            return false;
        }
    }

    @Override
    public void subscribe(String path, final IDataListener listener) {
        path = validatePath(path);
        this.zkClient.subscribeDataChanges(path, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) {
                listener.onSubscribe(IDataListener.NODE_CHANGED);
            }

            @Override
            public void handleDataDeleted(String dataPath) {
                listener.onSubscribe(IDataListener.NODE_DELETED);
            }
        });
    }

    @Override
    public void subscribeChild(String path, final IChildListener listener) {
        path = validatePath(path);
        this.zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) {
                listener.onSubscribe(currentChildren);
            }
        });
    }
}
