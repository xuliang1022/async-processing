package com.billion.async.processing.zookeeper;

import java.util.List;

/**
 * @author xuliang
 * @since 2018/12/26
 */
public interface IChildListener {

    /**
     * 监听子节点的变化
     *
     * @param children 子节点路径
     */
    void onSubscribe(List<String> children);

}
