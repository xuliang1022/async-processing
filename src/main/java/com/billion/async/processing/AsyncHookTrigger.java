package com.billion.async.processing;

/**
 * @author xuliang
 * @since 2018/12/28
 */
public interface AsyncHookTrigger {

    /**
     * 触发回调
     *
     * @param uniqueId 唯一标识
     */
    void trigger(String uniqueId);

}
