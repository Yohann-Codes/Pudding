package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import org.pudding.transport.api.Config;
import org.pudding.transport.common.Option;

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
    Class channelClass();

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
     * @return option.
     */
    Map<Option<?>, Object> options();
}
