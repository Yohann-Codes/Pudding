package org.pudding.transport.api;

/**
 * Pudding Channel.
 *
 * @author Yohann.
 */
public interface Channel {

    /**
     * @return 是否处于连接状态.
     */
    boolean isActive();

    /**
     * 向Channel写消息.
     *
     * @param msg
     * @return 用于添加异步结果监听器
     */
    Future write(Object msg);

    /**
     * 关闭当前Channel连接.
     */
    void close();
}
