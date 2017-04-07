package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.pudding.transport.api.Option;
import org.pudding.transport.api.OptionConfig;

/**
 * Netty configuration of connector.
 *
 * @author Yohann.
 */
public interface ConnectorNettyConfig {

    /**
     * Netty group.
     *
     * @param group
     */
    ConnectorNettyConfig group(EventLoopGroup group);

    /**
     * Returns the group.
     */
    EventLoopGroup group();

    /**
     * Netty channel.
     *
     * @param channel
     */
    ConnectorNettyConfig channel(Class channel);

    /**
     * Returns the class of {@link io.netty.channel.Channel}
     */
    Class channel();

    /**
     * Netty channel option.
     *
     * @param option
     * @param value
     * @param <T>
     */
    <T> ConnectorNettyConfig option(Option<T> option, T value);

    /**
     * Returns the option.
     */
    OptionConfig option();

    /**
     * Netty handler.
     *
     * @param initializer
     */
    ConnectorNettyConfig handler(ChannelInitializer<SocketChannel> initializer);

    /**
     * Returns the initializer.
     */
    ChannelInitializer<SocketChannel> handler();
}
