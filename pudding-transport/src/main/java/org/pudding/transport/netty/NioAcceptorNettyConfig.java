package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.pudding.transport.api.Option;
import org.pudding.transport.api.OptionConfig;
import org.pudding.transport.api.OptionGroup;

/**
 * The implementation of {@link AcceptorNettyConfig} based on NIO.
 *
 * @author Yohann.
 */
public class NioAcceptorNettyConfig implements AcceptorNettyConfig {

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Class channel = NioServerSocketChannel.class;
    private ChannelInitializer<SocketChannel> initializer;

    private OptionConfig parentOption = new ParentOption();
    private OptionConfig childOption = new ChildOption();

    public NioAcceptorNettyConfig(ChannelInitializer<SocketChannel> initializer) {
        this(null, null, null, initializer);
    }

    public NioAcceptorNettyConfig(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                                  Class channel, ChannelInitializer<SocketChannel> initializer) {
        checkNotNull(initializer, "initializer");
        init(bossGroup, workerGroup, channel);
        this.initializer = initializer;
    }

    private void init(EventLoopGroup bossGroup, EventLoopGroup workerGroup, Class channel) {
        if (bossGroup != null) {
            this.bossGroup = bossGroup;
        }
        if (workerGroup != null) {
            this.workerGroup = workerGroup;
        }
        if (channel != null) {
            this.channel = channel;
        }
    }


    @Override
    public AcceptorNettyConfig bossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        return this;
    }

    @Override
    public EventLoopGroup bossGroup() {
        return bossGroup;
    }

    @Override
    public AcceptorNettyConfig workerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
        return this;
    }

    @Override
    public EventLoopGroup workerGroup() {
        return workerGroup;
    }

    @Override
    public AcceptorNettyConfig channel(Class channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public Class channel() {
        return channel;
    }

    @Override
    public <T> AcceptorNettyConfig parentOption(Option<T> option, T value) {
        parentOption.setOption(option, value);
        return this;
    }

    @Override
    public <T> AcceptorNettyConfig childOption(Option<T> option, T value) {
        childOption.setOption(option, value);
        return this;
    }

    @Override
    public OptionGroup optionGroup() {
        return new NettyOptionGroup(parentOption, childOption);
    }

    @Override
    public AcceptorNettyConfig childHandler(ChannelInitializer<SocketChannel> initializer) {
        this.initializer = initializer;
        return this;
    }

    @Override
    public ChannelInitializer<SocketChannel> childHandler() {
        return initializer;
    }

    private <T> void checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
    }
}
