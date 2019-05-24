package com.billion.async.processing.zookeeper;

/**
 * @author xuliang
 * @since 2018/12/26
 */
public interface ZookeeperClient {

    /**
     * 获取zk服务器地址
     *
     * @return zk服务器地址
     */
    String getAddress();

    /**
     * 获取连接超时时间
     *
     * @return 连接超时时间
     */
    int getTimeout();

    /**
     * 创建zk节点，可以是多级节点
     *
     * @param path      节点路径
     * @param ephemeral 是否是临时节点 true:暂时节点  false:永久节点
     */
    void create(String path, boolean ephemeral);

    /**
     * 设置节点数据
     *
     * @param path 节点路径
     * @param data 数据
     */
    void setData(String path, byte[] data);

    /**
     * 删除节点路径
     *
     * @param path 节点路径
     */
    void delete(String path);

    /**
     * 判断是否存在此节点路径
     *
     * @param path 节点路径
     * @return true: 存在  false: 不存在
     */
    boolean exist(String path);

    /**
     * 订阅此节点，当此节点被删除或者数据被修改的时候将触发listener
     *
     * @param path      节点路径
     * @param listener  监听器
     */
    void subscribe(String path, IDataListener listener);

    /**
     * 订阅此节点下的子节点，当子节点发生变化的时候触发listener
     *
     * @param path      节点路径
     * @param listener  监听器
     */
    void subscribeChild(String path, IChildListener listener);

}
