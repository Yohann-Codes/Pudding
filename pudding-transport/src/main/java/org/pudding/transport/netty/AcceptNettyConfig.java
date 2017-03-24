package org.pudding.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.pudding.transport.api.Config;
import org.pudding.transport.common.Option;

import java.util.HashMap;
import java.util.Map;

/**
 * 接收端Netty配置.
 *
 * @author Yohann.
 */
public class AcceptNettyConfig implements IAcceptNettyConfig {

    private Map<Option<?>, Object> options;  // save parent option, singleton
    private Map<Option<?>, Object> childOptions;  // save child option, singleton

    // EventLoopGroup default value:
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Class<? extends Channel> channelClass;

    private ChannelInitializer initializer;

    public AcceptNettyConfig(Class<? extends Channel> channelClass, ChannelInitializer initializer) {
        this(null, null, channelClass, initializer);
    }

    public AcceptNettyConfig(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                             Class<? extends Channel> channelClass, ChannelInitializer initializer) {
        checkGroup(bossGroup, workerGroup);
        validate(channelClass, initializer);
        this.channelClass = channelClass;
        this.initializer = initializer;
        options = options();
        childOptions = childOptions();
    }

    private void checkGroup(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        // Set the default values if the null
        if (bossGroup != null) {
            this.bossGroup = bossGroup;
        }
        if (workerGroup != null) {
            this.workerGroup = workerGroup;
        }
    }

    @Override
    public <T> Config option(Option<T> option, T value) {
        validate(option, value);
        options.put(option, value);
        return this;
    }

    @Override
    public <T> Config childOption(Option<T> option, T value) {
        validate(option, value);
        childOptions.put(option, value);
        return this;
    }

    @Override
    public IAcceptNettyConfig bossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        return this;
    }

    @Override
    public EventLoopGroup bossGroup() {
        return bossGroup;
    }

    @Override
    public IAcceptNettyConfig workerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
        return this;
    }

    @Override
    public EventLoopGroup workerGroup() {
        return workerGroup;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IAcceptNettyConfig channel(Class channelClass) {
        this.channelClass = channelClass;
        return this;
    }

    @Override
    public Class channel() {
        return channelClass;
    }

    @Override
    public IAcceptNettyConfig childHandler(ChannelInitializer initializer) {
        this.initializer = initializer;
        return this;
    }

    @Override
    public ChannelInitializer childHandler() {
        return initializer;
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

    @Override
    public Map<Option<?>, Object> childOptions() {
        if (childOptions == null) {
            synchronized (this) {
                if (childOptions == null) {
                    childOptions = new HashMap<>();
                }
            }
        }
        return childOptions;
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