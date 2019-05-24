package com.billion.async.processing;

/**
 * @author xuliang
 * @since 2019/05/13
 */
public enum HookRegisterState {

    /** 创建状态 */
    CREATED {
        @Override
        public boolean isInit() {
            return true;
        }

        @Override
        public boolean isFinalState() {
            return false;
        }
    },

    /** 准备状态 */
    READY {
        @Override
        public boolean isInit() {
            return false;
        }

        @Override
        public boolean isFinalState() {
            return false;
        }
    },

    /** 完成状态 */
    COMPLETED {
        @Override
        public boolean isInit() {
            return false;
        }

        @Override
        public boolean isFinalState() {
            return true;
        }
    },

    /** 超时取消状态 */
    CANCELED {
        @Override
        public boolean isInit() {
            return false;
        }

        @Override
        public boolean isFinalState() {
            return true;
        }
    };

    /**
     * 是否是初始化状态，只有初始化的状态才能接收任务
     *
     * @return true：初始化状态
     */
    public abstract boolean isInit();

    /**
     * 当前状态是否是终态
     *
     * @return true: 是终态
     */
    public abstract boolean isFinalState();

    /**
     * 当前任务是否处于就绪状态
     *
     * @return true:处于就绪状态
     */
    public boolean isReady() {
        return this.equals(READY);
    }

}
