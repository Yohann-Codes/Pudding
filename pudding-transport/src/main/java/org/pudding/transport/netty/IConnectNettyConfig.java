package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.pudding.transport.abstraction.Config;
import org.pudding.transport.options.Option;

import java.util.Map;

/**
 * @author Yohann.
 */
public interface IConnectNettyConfig extends Config {

    /**
     * Config workerGroup.
     *
     * @param group
     */
    IConnectNettyConfig group(EventLoopGroup group);

    /**
     * @return workerGroup.
     */
    EventLoopGroup group();

    /**
     * Config channel class.
     *
     * @param channelClass
     */
    IConnectNettyConfig channel(Class channelClass);

    /**
     * @return channel class.
     */
    Class channel();

    /**
     * Config ChannelInitializer.
     *
     * @param initializer
     */
    IConnectNettyConfig handler(ChannelInitializer initializer);

    /**
     * @return childHandler.
     */
    ChannelInitializer handler();

    /**
     * @return options.
     */
    Map<Option<?>, Object> options();
}
