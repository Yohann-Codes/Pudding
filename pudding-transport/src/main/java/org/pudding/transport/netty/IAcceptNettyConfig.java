package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import org.pudding.transport.api.Config;
import org.pudding.transport.api.Processor;
import org.pudding.transport.api.ProcessorHandler;

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
    IAcceptNettyConfig channelClass(Class channelClass);

    /**
     * @return channel class.
     */
    Class channelClass();

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
     * 关联用户自定义Processor.
     * 如果在某个Handler中需要使用Processor给其它模块传递消息就必须调用此方法.
     *
     * @param processor
     */
    void processor(ProcessorHandler processorHandler, Processor processor);

    /**
     * @return option.
     */
    Map<Option<?>, Object> options();

    /**
     * @return child option.
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
