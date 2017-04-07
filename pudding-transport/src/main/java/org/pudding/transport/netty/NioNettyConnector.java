package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;
import org.pudding.common.constant.IdleTime;
import org.pudding.transport.api.ChannelFuture;
import org.pudding.transport.api.Option;
import org.pudding.transport.api.OptionConfig;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.handler.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.pudding.transport.api.Option.*;

/**
 * The client side based on Netty NIO.
 *
 * @author Yohann.
 */
public class NioNettyConnector extends AbstractNettyConnector {
    private static final Logger logger = Logger.getLogger(NioNettyConnector.class);

    private SocketAddress remoteAddress;
    private Processor processor;

    public NioNettyConnector(Processor processor) {
        this(processor, null);
    }

    public NioNettyConnector(Processor processor, ConnectorNettyConfig config) {
        super(config);

        checkNotNull(processor, "processor");
        this.processor = processor;

        defaultOption();
        configNetty();
    }

    private void defaultOption() {
        config.option(Option.SO_KEEPALIVE, true);
        config.option(Option.TCP_NODELAY, true);
    }

    @SuppressWarnings("unchecked")
    private void configNetty() {
        Class channel = config.channel();
        EventLoopGroup group = config.group();
        ChannelInitializer<SocketChannel> initializer = config.handler();

        bootstrap.group(group)
                .channel(channel)
                .handler(initializer);

        setOptions();
    }

    private void setOptions() {
        OptionConfig option = option();
        bootstrap.option(ChannelOption.TCP_NODELAY, option.getOption(TCP_NODELAY));
        bootstrap.option(ChannelOption.SO_KEEPALIVE, option.getOption(SO_KEEPALIVE));
        bootstrap.option(ChannelOption.SO_REUSEADDR, option.getOption(SO_REUSEADDR));
        bootstrap.option(ChannelOption.ALLOW_HALF_CLOSURE, option.getOption(ALLOW_HALF_CLOSURE));
        if (option.getOption(SO_RCVBUF) > 0) {
            bootstrap.option(ChannelOption.SO_RCVBUF, option.getOption(SO_RCVBUF));
        }
        if (option.getOption(SO_SNDBUF) > 0) {
            bootstrap.option(ChannelOption.SO_SNDBUF, option.getOption(SO_SNDBUF));
        }
        if (option.getOption(SO_LINGER) > 0) {
            bootstrap.option(ChannelOption.SO_LINGER, option.getOption(SO_LINGER));
        }
        if (option.getOption(IP_TOS) > 0) {
            bootstrap.option(ChannelOption.IP_TOS, option.getOption(IP_TOS));
        }

        setOptions0(option);
    }

    @Override
    public SocketAddress remoteAddress() {
        return remoteAddress;
    }

    @Override
    public ChannelFuture connect(String host, int port) throws InterruptedException {
        return connect(new InetSocketAddress(host, port));
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) throws InterruptedException {
        this.remoteAddress = remoteAddress;

        io.netty.channel.ChannelFuture future = bootstrap.connect(remoteAddress).sync();
        logger.info("connect to " + remoteAddress);

        return new NettyChannelFuture(future);
    }

    @Override
    public OptionConfig option() {
        return config.option();
    }

    @Override
    public void shutdownGracefully() {
        config.group().shutdownGracefully();
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
                pipeline.addLast(new IdleStateHandler(0, IdleTime.WRITER_IDLE_TIME, 0));
                pipeline.addLast(new HeartbeatHandlerC());
                pipeline.addLast(new ConnectorHandler(processor));
            }
        };
    }

    @Override
    protected ConnectorNettyConfig newConnectorNettyConfig(ChannelInitializer<SocketChannel> initializer) {
        return new NioConnectorNettyConfig(initializer);
    }

    private <T> void checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
    }
}