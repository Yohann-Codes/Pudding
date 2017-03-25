package org.pudding.transport.api;

import org.pudding.transport.common.MessageHolder;

/**
 * 由其它模块实现.
 *
 * @author Yohann.
 */
public interface Processor {

    /**
     * 处理网络数据.
     *
     * @param channel
     * @param holder
     */
    void handleMessage(Channel channel, MessageHolder holder);
}
