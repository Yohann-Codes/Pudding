package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import org.pudding.transport.abstraction.Acceptor;

/**
 * @author Yohann.
 */
public interface INettyAcceptor extends Acceptor {

    /**
     * @return bossGroup.
     */
    EventLoopGroup bossGroup();

    /**
     * @return workerGroup.
     */
    EventLoopGroup workerGroup();

    /**
     * @return channel class.
     */
    Class channel();

    /**
     * @return childHandler.
     */
    ChannelInitializer childHandler();
}
