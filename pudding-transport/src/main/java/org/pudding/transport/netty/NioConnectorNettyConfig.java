package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.pudding.transport.api.Option;
import org.pudding.transport.api.OptionConfig;

/**
 * The implementation of {@link ConnectorNettyConfig} based on NIO.
 *
 * @author Yohann.
 */
public class NioConnectorNettyConfig implements ConnectorNettyConfig {

    private EventLoopGroup group = new NioEventLoopGroup();

    private Class channel = NioSocketChannel.class;
    private ChannelInitializer<SocketChannel> initializer;

    private OptionConfig option = new ChildOption();

    public NioConnectorNettyConfig(ChannelInitializer<SocketChannel> initializer) {
        this(null, null, initializer);
    }

    public NioConnectorNettyConfig(EventLoopGroup group, Class channel,
                                   ChannelInitializer<SocketChannel> initializer) {
        checkNotNull(initializer, "initializer");
        init(group, channel);
        this.initializer = initializer;
    }

    private void init(EventLoopGroup group, Class channel) {
        if (group != null) {
            this.group = group;
        }
        if (channel != null) {
            this.channel = channel;
        }
    }


    @Override
    public ConnectorNettyConfig group(EventLoopGroup group) {
        this.group = group;
        return this;
    }

    @Override
    public EventLoopGroup group() {
        return group;
    }

    @Override
    public ConnectorNettyConfig channel(Class channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public Class channel() {
        return channel;
    }

    @Override
    public <T> ConnectorNettyConfig option(Option<T> option, T value) {
        this.option.setOption(option, value);
        return this;
    }

    @Override
    public OptionConfig option() {
        return option;
    }

    @Override
    public ConnectorNettyConfig handler(ChannelInitializer<SocketChannel> initializer) {
        this.initializer = initializer;
        return this;
    }

    @Override
    public ChannelInitializer<SocketChannel> handler() {
        return initializer;
    }

    private <T> void checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
    }
}
