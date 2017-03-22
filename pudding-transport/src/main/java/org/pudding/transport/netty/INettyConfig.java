package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.SocketChannel;
import org.pudding.transport.abstraction.Config;
import org.pudding.transport.options.Option;

import java.util.Map;

/**
 * Netty配置接口.
 *
 * @author Yohann.
 */
public interface INettyConfig extends Config {

    /**
     * Config bossGroup.
     *
     * @param bossGroup
     */
    INettyConfig bossGroup(EventLoopGroup bossGroup);

    /**
     * @return bossGroup.
     */
    EventLoopGroup bossGroup();

    /**
     * Config workerGroup.
     *
     * @param workerGroup
     */
    INettyConfig workerGroup(EventLoopGroup workerGroup);

    /**
     * @return workerGroup.
     */
    EventLoopGroup workerGroup();

    /**
     * Config channel class.
     *
     * @param channelClass
     */
    INettyConfig channel(Class<? extends ServerChannel> channelClass);

    /**
     * @return channel class.
     */
    Class<? extends ServerChannel> channel();

    /**
     * Config ChannelInitializer.
     *
     * @param initializer
     */
    INettyConfig childHandler(ChannelInitializer initializer);

    /**
     * @return childHandler.
     */
    ChannelInitializer childHandler();

    /**
     * Add handlers.
     */
    void addHandlers(SocketChannel ch);

    /**
     * @return options.
     */
    Map<Option<?>, Object> options();

    /**
     * @return child options.
     */
    Map<Option<?>, Object> childOptions();
}
