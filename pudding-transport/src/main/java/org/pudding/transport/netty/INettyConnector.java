package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import org.pudding.transport.api.Connector;

/**
 * @author Yohann.
 */
public interface INettyConnector extends Connector {

    /**
     * @return workerGroup.
     */
    EventLoopGroup group();

    /**
     * @return channel class.
     */
    Class channelClass();

    /**
     * @return handler.
     */
    ChannelInitializer handler();
}
