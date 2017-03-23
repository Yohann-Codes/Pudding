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
public interface IAcceptNettyConfig extends Config {

    /**
     * Config bossGroup.
     *
     * @param bossGroup
     */
    IAcceptNettyConfig bossGroup(EventLoopGroup bossGroup);

    /**
     * @return bossGroup.
     */
    EventLoopGroup bossGroup();

    /**
     * Config workerGroup.
     *
     * @param workerGroup
     */
    IAcceptNettyConfig workerGroup(EventLoopGroup workerGroup);

    /**
     * @return workerGroup.
     */
    EventLoopGroup workerGroup();

    /**
     * Config channel class.
     *
     * @param channelClass
     */
    IAcceptNettyConfig channel(Class channelClass);

    /**
     * @return channel class.
     */
    Class channel();

    /**
     * Config ChannelInitializer.
     *
     * @param initializer
     */
    IAcceptNettyConfig childHandler(ChannelInitializer initializer);

    /**
     * @return childHandler.
     */
    ChannelInitializer childHandler();

    /**
     * @return options.
     */
    Map<Option<?>, Object> options();

    /**
     * @return child options.
     */
    Map<Option<?>, Object> childOptions();

    /**
     * 设置Pudding子通道选项.
     *
     * @param option
     * @param <T>
     */
    <T> Config childOption(Option<T> option, T value);
}
