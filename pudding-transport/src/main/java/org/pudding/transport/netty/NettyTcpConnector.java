package org.pudding.transport.netty;

import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;
import org.pudding.common.constant.IdleTime;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelManager;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.connection.ConnectionWatchdog;
import org.pudding.transport.netty.handler.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * The TCP impelementation of acceptor based Netty.
 *
 * @author Yohann.
 */
public class NettyTcpConnector extends NettyConnector {
    private static final Logger logger = Logger.getLogger(NettyTcpConnector.class);

    // Reusable handlers (stateless)
    private final ProtocolEncoder protocolEncoder = new ProtocolEncoder();
    private final HeartbeatHandlerC heartbeatHandler = new HeartbeatHandlerC();
    private final ConnectorHandler connectorHandler = new ConnectorHandler(this);

    private boolean epoll; // Use epoll of Linux

    private boolean processor = false;

    public NettyTcpConnector() {
        super();
        epoll = true;
        init();
    }

    public NettyTcpConnector(int nWorkers) {
        super(nWorkers);
        epoll = true;
        init();
    }

    public NettyTcpConnector(boolean epoll) {
        super();
        this.epoll = epoll;
        init();
    }

    public NettyTcpConnector(int nWorkers, boolean epoll) {
        super(nWorkers);
        this.epoll = epoll;
        init();
    }

    @Override
    protected void init() {
        super.init();

        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
    }

    @Override
    public SocketAddress remoteAddress() {
        return remoteAddress;
    }

    @Override
    public Channel connect(String host, int port) throws InterruptedException {
        return connect(new InetSocketAddress(host, port));
    }

    @Override
    public Channel connect(SocketAddress remoteAddress) throws InterruptedException {
        super.connect(remoteAddress);
        checkProcessor();

        final ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, timer, remoteAddress) {

            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[] {
                        this,
                        new ProtocolDecoder(),
                        protocolEncoder,
                        new IdleStateHandler(0, IdleTime.WRITER_IDLE_TIME, 0),
                        heartbeatHandler,
                        connectorHandler
                };
            }
        };

        bootstrap.group(group());

        if (isSupportEpoll()) {
            bootstrap.channelFactory(TcpChannelFactory.EPOLL_FACTORY_CONNECTOR);
        } else {
            bootstrap.channelFactory(TcpChannelFactory.NIO_FACTORY_CONNECTOR);
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(watchdog.handlers());
            }
        });

        ChannelFuture future = bootstrap.connect(remoteAddress).sync();

        return new NettyChannel(future.channel());
    }

    @Override
    public void processor(Processor processor) {
        checkNotNull(processor, "processor");
        this.processor = true;
        connectorHandler.setProcessor(processor);
    }

    @Override
    public ChannelManager channelManager() {
        return channelManager;
    }

    @Override
    public void shutdownGracefully() {
        try {
            group().shutdownGracefully().sync();
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    protected EventLoopGroup initEventLoopGroup(int nThreads, ThreadFactory factory) {
        return isSupportEpoll() ?
                new EpollEventLoopGroup(nThreads, factory) : new NioEventLoopGroup(nThreads, factory);
    }

    /**
     * Check whether the current platform support epoll.
     */
    private boolean isSupportEpoll() {
        return epoll && EpollSupport.isSupportEpoll();
    }

    /**
     * Check processor.
     */
    private void checkProcessor() {
        if (!processor) {
            throw new IllegalStateException("invalid processor");
        }
    }
}
