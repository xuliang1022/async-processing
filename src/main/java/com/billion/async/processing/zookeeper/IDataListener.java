package com.billion.async.processing.zookeeper;

/**
 * @author xuliang
 * @since 2018/12/26
 */
public interface IDataListener {

    /** 数据发生修改 */
    int NODE_CHANGED = 0;

    /** 数据被删除 */
    int NODE_DELETED = 1;

    /**
     * 监听节点数据发生变法
     *
     * @param state {@code IDataListener#NODE_CHANGED}, {@code IDataListener#NODE_DELETED}
     */
    void onSubscribe(int state);

}
