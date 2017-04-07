package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.pudding.transport.api.Option;
import org.pudding.transport.api.OptionGroup;

/**
 * Netty configuration of acceptor.
 *
 * @author Yohann.
 */
public interface AcceptorNettyConfig {

    /**
     * Netty bossGroup.
     *
     * @param bossGroup
     */
    AcceptorNettyConfig bossGroup(EventLoopGroup bossGroup);

    /**
     * Returns the bossGroup.
     */
    EventLoopGroup bossGroup();

    /**
     * Netty workerGroup.
     *
     * @param workerGroup
     */
    AcceptorNettyConfig workerGroup(EventLoopGroup workerGroup);

    /**
     * Returns the workerGroup.
     */
    EventLoopGroup workerGroup();

    /**
     * Netty channel.
     *
     * @param channel
     */
    AcceptorNettyConfig channel(Class channel);

    /**
     * Returns the class of {@link io.netty.channel.Channel}.
     */
    Class channel();

    /**
     * Netty parent channel option.
     *
     * @param option
     * @param value
     * @param <T>
     */
    <T> AcceptorNettyConfig parentOption(Option<T> option, T value);

    /**
     * Netty child channel option.
     *
     * @param option
     * @param value
     * @param <T>
     */
    <T> AcceptorNettyConfig childOption(Option<T> option, T value);

    /**
     * Returns the optionGroup.
     */
    OptionGroup optionGroup();

    /**
     * Netty child handler.
     *
     * @param initializer
     */
    AcceptorNettyConfig childHandler(ChannelInitializer<SocketChannel> initializer);

    /**
     * Returns the childHandler.
     */
    ChannelInitializer<SocketChannel> childHandler();
}
