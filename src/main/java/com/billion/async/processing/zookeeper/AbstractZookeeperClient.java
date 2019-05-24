package com.billion.async.processing.zookeeper;

/**
 * @author xuliang
 * @since 2019/05/13
 */
public abstract class AbstractZookeeperClient implements ZookeeperClient {

    /** 默认连接超时时间 */
    public static final int DEFAULT_TIMEOUT = 5000;

    /** 超时时间 */
    private int timeout;

    /** zk服务器地址 */
    private String address;

    public AbstractZookeeperClient(String address) {
        this(address, DEFAULT_TIMEOUT);
    }

    public AbstractZookeeperClient(String address, int timeout) {
        this.address = address;
        this.timeout = timeout <= 0 ? DEFAULT_TIMEOUT : timeout;
    }

    @Override
    public void create(String path, boolean ephemeral) {
        path = validatePath(path);
        int i = path.lastIndexOf('/');
        if (i > 0) {
            String parentPath = path.substring(0, i);
            if (!exist(parentPath)) {
                create(parentPath, false);
            }
        }
        if (ephemeral) {
            createEphemeral(path);
        } else {
            createPersistent(path);
        }
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    /**
     * 创建临时节点
     *
     * @param path 节点路径
     */
    protected abstract void createEphemeral(String path);

    /**
     * 创建持久化节点
     *
     * @param path 节点路径
     */
    protected abstract void createPersistent(String path);

    /**
     * 检验路径是否合法，如果不合法则补全
     *
     * @param path 节点路径
     * @return 检验后的路径
     */
    protected String validatePath(String path) {
        if (path == null || path.isEmpty()) {
            throw new RuntimeException("path is empty.");
        }

        if (path.startsWith("/")) {
            return path;
        }

        return "/" + path;
    }

}
