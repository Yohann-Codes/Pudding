package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;
import org.pudding.common.constant.IdleTime;
import org.pudding.transport.api.*;
import org.pudding.transport.netty.handler.AcceptorHandler;
import org.pudding.transport.netty.handler.HeartbeatHandlerS;
import org.pudding.transport.netty.handler.ProtocolDecoder;
import org.pudding.transport.netty.handler.ProtocolEncoder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.pudding.transport.api.Option.*;

/**
 * The server side based on Netty NIO.
 *
 * @author Yohann.
 */
public class NioNettyAcceptor extends AbstractNettyAcceptor {
    private static final Logger logger = Logger.getLogger(NioNettyAcceptor.class);

    private SocketAddress localAddress;
    private Processor processor;

    public NioNettyAcceptor(Processor processor) {
        this(processor, null);
    }

    public NioNettyAcceptor(Processor processor, AcceptorNettyConfig config) {
        super(config);

        checkNotNull(processor, "processor");
        this.processor = processor;

        defaultOption();
        configNetty();
    }

    private void defaultOption() {
        // Parent option
        config.parentOption(Option.SO_BACKLOG, 1024);

        // Child option
        config.childOption(Option.SO_KEEPALIVE, true);
        config.childOption(Option.TCP_NODELAY, true);
    }

    @SuppressWarnings("unchecked")
    private void configNetty() {
        Class channel = config.channel();
        EventLoopGroup bossGroup = config.bossGroup();
        EventLoopGroup workerGroup = config.workerGroup();
        ChannelInitializer<SocketChannel> initializer = config.childHandler();

        bootstrap.group(bossGroup, workerGroup)
                .channel(channel)
                .childHandler(initializer);

        setOptions();
    }

    private void setOptions() {
        OptionGroup optionGroup = config.optionGroup();

        // Parent option
        OptionConfig parentOption = optionGroup.parentOption();
        bootstrap.option(ChannelOption.SO_REUSEADDR, parentOption.getOption(SO_REUSEADDR));
        if (parentOption.getOption(SO_BACKLOG) > 0) {
            bootstrap.option(ChannelOption.SO_BACKLOG, parentOption.getOption(SO_BACKLOG));
        }
        if (parentOption.getOption(SO_RCVBUF) > 0) {
            bootstrap.option(ChannelOption.SO_RCVBUF, parentOption.getOption(SO_RCVBUF));
        }
        setParentOptions(parentOption);

        // Child option
        OptionConfig childOption = optionGroup.childOption();
        bootstrap.childOption(ChannelOption.TCP_NODELAY, childOption.getOption(TCP_NODELAY));
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, childOption.getOption(SO_KEEPALIVE));
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, childOption.getOption(SO_REUSEADDR));
        bootstrap.childOption(ChannelOption.ALLOW_HALF_CLOSURE, childOption.getOption(ALLOW_HALF_CLOSURE));
        if (childOption.getOption(SO_RCVBUF) > 0) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, childOption.getOption(SO_RCVBUF));
        }
        if (childOption.getOption(SO_SNDBUF) > 0) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, childOption.getOption(SO_SNDBUF));
        }
        if (childOption.getOption(SO_LINGER) > 0) {
            bootstrap.childOption(ChannelOption.SO_LINGER, childOption.getOption(SO_LINGER));
        }
        if (childOption.getOption(IP_TOS) > 0) {
            bootstrap.childOption(ChannelOption.IP_TOS, childOption.getOption(IP_TOS));
        }
        setChildOptions(childOption);
    }

    @Override
    public SocketAddress localAddress() {
        return localAddress;
    }

    @Override
    public ChannelFuture bind(int port) throws InterruptedException {
        return bind(null, port);
    }

    @Override
    public ChannelFuture bind(String host, int port) throws InterruptedException {
        return bind(new InetSocketAddress(host, port));
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) throws InterruptedException {
        this.localAddress = localAddress;

        io.netty.channel.ChannelFuture future = bootstrap.bind(localAddress).sync();
        logger.info("listening on " + localAddress);

        // Wait until the server socket is closed.
        future.channel().closeFuture().sync();
        logger.info("closing on " + localAddress);

        return new NettyChannelFuture(future);
    }

    @Override
    public OptionGroup optionGroup() {
        return config.optionGroup();
    }

    @Override
    public void shutdownGracefully() {
        config.bossGroup().shutdownGracefully();
        config.workerGroup().shutdownGracefully();
    }

    @Override
    protected ChannelInitializer<SocketChannel> initInitializer() {
        return new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // Add handler...
                pipeline.addLast(new ProtocolDecoder());
                pipeline.addLast(new ProtocolEncoder());
                pipeline.addLast(new IdleStateHandler(IdleTime.READER_IDLE_TIME, 0, 0));
                pipeline.addLast(new HeartbeatHandlerS());
                pipeline.addLast(new AcceptorHandler(processor));
            }
        };
    }

    @Override
    protected AcceptorNettyConfig newAcceptorNettyConfig(ChannelInitializer<SocketChannel> initializer) {
        return new NioAcceptorNettyConfig(initializer);
    }

    private <T> void checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
    }
}