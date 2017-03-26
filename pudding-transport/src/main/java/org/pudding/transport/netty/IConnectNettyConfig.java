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
    IConnectNettyConfig channelClass(Class channelClass);

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
}
