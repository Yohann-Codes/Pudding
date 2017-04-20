package org.pudding.transport.netty;

import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;
import org.pudding.common.constant.IdleTime;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.handler.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * The TCP impelementation of acceptor based Netty.
 *
 * @author Yohann.
 */
public class NettyTcpAcceptor extends NettyAcceptor {
    private static final Logger logger = Logger.getLogger(NettyTcpAcceptor.class);

    // Reusable handlers (stateless)
    private final ProtocolEncoder protocolEncoder = new ProtocolEncoder();
    private final AcceptorHandler acceptorHandler = new AcceptorHandler();

    private boolean epoll; // Use epoll of Linux

    private boolean processor = false;

    public NettyTcpAcceptor() {
        super();
        epoll = true;
        init();
    }

    public NettyTcpAcceptor(int nWorkers) {
        super(nWorkers);
        epoll = true;
        init();
    }

    public NettyTcpAcceptor(boolean epoll) {
        super();
        this.epoll = epoll;
        init();
    }

    public NettyTcpAcceptor(int nWorkers, boolean epoll) {
        super(nWorkers);
        this.epoll = epoll;
        init();
    }

    @Override
    protected void init() {
        super.init();

        // Parent option
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);

        // Child option
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    }

    @Override
    protected void doInit() {
        if (isSupportEpoll()) {
            bootstrap.channelFactory(TcpChannelFactory.EPOLL_FACTORY_ACCEPTRO);
        } else {
            bootstrap.channelFactory(TcpChannelFactory.NIO_FACTORY_ACCEPTRO);
        }
    }

    @Override
    public Channel bind(int port) throws InterruptedException {
        return bind(new InetSocketAddress(port));
    }

    @Override
    public Channel bind(String host, int port) throws InterruptedException {
        return bind(new InetSocketAddress(host, port));
    }

    @Override
    public Channel bind(SocketAddress localAddress) throws InterruptedException {
        checkProcessor();

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(
                        new ProtocolDecoder(),
                        protocolEncoder,
                        new IdleStateHandler(IdleTime.READER_IDLE_TIME, 0, 0),
                        new HeartbeatHandlerS(),
                        acceptorHandler);
            }
        });

        ChannelFuture future = bootstrap.bind(localAddress).sync();
        logger.info("listening on " + localAddress);

        return NettyChannelFactory.newChannel(future.channel());
    }

    @Override
    public void withProcessor(Processor processor) {
        checkNotNull(processor, "processor");
        this.processor = true;
        acceptorHandler.setProcessor(processor);
    }

    @Override
    public void shutdownGracefully() {
        try {
            bossGroup().shutdownGracefully().sync();
            workerGroup().shutdownGracefully().sync();
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
            throw new IllegalStateException("invalid processor, please set processor");
        }
    }
}
