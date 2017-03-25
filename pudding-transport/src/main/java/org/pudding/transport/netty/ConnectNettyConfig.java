package org.pudding.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.pudding.transport.api.Config;
import org.pudding.transport.api.Processor;
import org.pudding.transport.common.Option;
import org.pudding.transport.api.ProcessorHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接端Netty配置.
 *
 * @author Yohann.
 */
public class ConnectNettyConfig implements IConnectNettyConfig {

    private Map<Option<?>, Object> options;  // save option, singleton

    // EventLoopGroup default value:
    private EventLoopGroup group = new NioEventLoopGroup();

    private Class<? extends Channel> channelClass;

    private ChannelInitializer initializer;

    public ConnectNettyConfig(Class<? extends Channel> channelClass, ChannelInitializer initializer) {
        this(null, channelClass, initializer);
    }

    public ConnectNettyConfig(EventLoopGroup group,
                              Class<? extends Channel> channelClass, ChannelInitializer initializer) {
        checkGroup(group);
        validate(channelClass, initializer);
        this.channelClass = channelClass;
        this.initializer = initializer;
        options = options();
    }

    private void checkGroup(EventLoopGroup group) {
        // Set the default values if the null
        if (group != null) {
            this.group = group;
        }
    }

    @Override
    public <T> Config option(Option<T> option, T value) {
        validate(option, value);
        options.put(option, value);
        return this;
    }

    @Override
    public IConnectNettyConfig group(EventLoopGroup group) {
        this.group = group;
        return this;
    }

    @Override
    public EventLoopGroup group() {
        return group;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IConnectNettyConfig channelClass(Class channelClass) {
        this.channelClass = channelClass;
        return this;
    }

    @Override
    public Class channelClass() {
        return channelClass;
    }

    @Override
    public IConnectNettyConfig handler(ChannelInitializer initializer) {
        this.initializer = initializer;
        return this;
    }

    @Override
    public ChannelInitializer handler() {
        return initializer;
    }

    @Override
    public void processor(ProcessorHandler processorHandler, Processor processor) {
        processorHandler.processor(processor);
    }

    @Override
    public Map<Option<?>, Object> options() {
        if (options == null) {
            synchronized (this) {
                if (options == null) {
                    options = new HashMap<>();
                }
            }
        }
        return options;
    }

    private <T> void validate(Option<T> option, T value) {
        if (option == null) {
            throw new NullPointerException("option == null");
        }
        if (value == null) {
            throw new NullPointerException("value == null");
        }
    }

    private void validate(Class<? extends Channel> channelClass, ChannelInitializer initializer) {
        if (channelClass == null) {
            throw new NullPointerException("channelClass == null");
        }
        if (initializer == null) {
            throw new NullPointerException("initializer == null");
        }
    }
}