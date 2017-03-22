package org.pudding.transport.netty;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.pudding.transport.abstraction.Config;
import org.pudding.transport.options.Option;

import java.util.HashMap;
import java.util.Map;

/**
 * Netty默认配置.
 *
 * @author Yohann.
 */
public class NettyConfig implements INettyConfig {

    private Map<Option<?>, Object> options;  // save options, singleton
    private Map<Option<?>, Object> childOptions;  // save child options, singleton

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Class<? extends ServerChannel> channelClass;
    private ChannelInitializer initializer;

    public NettyConfig() {
        options = options();
        childOptions = childOptions();
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
    public INettyConfig bossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup ;
        return this;
    }

    @Override
    public EventLoopGroup bossGroup() {
        return bossGroup;
    }

    @Override
    public INettyConfig workerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
        return this;
    }

    @Override
    public EventLoopGroup workerGroup() {
        return workerGroup;
    }

    @Override
    public INettyConfig channel(Class<? extends ServerChannel> channelClass) {
        this.channelClass = channelClass;
        return this;
    }

    @Override
    public Class<? extends ServerChannel> channel() {
        return channelClass;
    }

    @Override
    public INettyConfig childHandler(ChannelInitializer initializer) {
        this.initializer = initializer;
        return this;
    }

    @Override
    public ChannelInitializer childHandler() {
        return initializer;
    }

    @Override
    public void addHandlers(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
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
        if (option == null || value == null) {
            throw new NullPointerException("option == null || value == null");
        }
    }
}