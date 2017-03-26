package org.pudding.transport.api;

import org.pudding.common.protocol.MessageHolder;

/**
 * 由其它模块实现.
 *
 * @author Yohann.
 */
public interface Processor {

    /**
     * 读取网络数据.
     *
     * @param channel
     * @param holder
     */
    void channelRead(Channel channel, MessageHolder holder);

    /**
     * Channel连接.
     *
     * @param channel
     */
    void channelActive(Channel channel);

    /**
     * Channel断开连接,
     *
     * @param channel
     */
    void channelInactive(Channel channel);
}
