package org.pudding.transport.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import org.apache.log4j.Logger;
import org.pudding.transport.abstraction.Config;
import org.pudding.transport.exception.IllegalOptionException;
import org.pudding.transport.options.Option;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

/**
 * 基于Netty的Acceptor实现.
 *
 * @author Yohann.
 */
public class NettyAcceptor extends ConfigOptions implements INettyAcceptor {

    private static final Logger logger = Logger.getLogger(NettyAcceptor.class);

    private INettyConfig nettyConfig;
    private SocketAddress localAddress;

    /**
     * Default NettyConfig.
     */
    public NettyAcceptor() {
        nettyConfig = new DefaultAcceptConfig();
    }

    /**
     * Custom NettyConfig.
     *
     * @param nettyConfig
     */
    public NettyAcceptor(INettyConfig nettyConfig) {
        this.nettyConfig = nettyConfig;
    }

    @Override
    public SocketAddress localAddress() {
        return localAddress;
    }

    @Override
    public void bind(int port) {
        bind(new InetSocketAddress(port));
    }

    @Override
    public void bind(String host, int port) {
        bind(new InetSocketAddress(host, port));
    }

    @Override
    public void bind(SocketAddress localAddress) {
        this.localAddress = localAddress;
        try {
            doBind();
        } catch (InterruptedException e) {
            logger.warn("bind exception", e);
        } finally {
            shutdownGracefully();
        }
    }

    @SuppressWarnings("unchecked")
    private void doBind() throws InterruptedException, IllegalOptionException {
        validate(localAddress, bootstrap);
        bootstrap.group(bossGroup(), workerGroup())
                .channel(channel())
                .childHandler(childHandler());

        if (!setOption()) {
            throw new IllegalOptionException("setOption Exception");
        }

        ChannelFuture future = bootstrap.bind(localAddress).sync();
        logger.info("Server has started, listening on " + localAddress);

        future.channel().closeFuture().sync();
    }

    private boolean setOption() {
        // parent
        Map<Option<?>, Object> options = nettyConfig.options();
        for (Map.Entry<Option<?>, Object> entry : options.entrySet()) {
            Option<?> option = entry.getKey();
            Object value = entry.getValue();
            if (option == Option.SO_BACKLOG) {
                parent.setSoBacklog((Integer) value);
            } else if (option == Option.SO_REUSEADDR) {
                parent.setSoReuseaddr((Boolean) value);
            } else if (option == Option.SO_RCVBUF) {
                parent.setSoRcvbuf((Integer) value);
            } else {
                setOption0(parent, option, value);
            }
        }

        // child
        Map<Option<?>, Object> childOptions = nettyConfig.childOptions();
        for (Map.Entry<Option<?>, Object> entry : childOptions.entrySet()) {
            Option<?> option = entry.getKey();
            Object value = entry.getValue();
            if (option == Option.SO_SNDBUF) {
                child.setSoSndbuf((Integer) value);
            } else if (option == Option.SO_RCVBUF) {
                child.setSoRcvbuf((Integer) value);
            } else if (option == Option.TCP_NODELAY) {
                child.setTcpNodelay((Boolean) value);
            } else if (option == Option.SO_KEEPALIVE) {
                child.setSoKeepalive((Boolean) value);
            } else if (option == Option.SO_KEEPALIVE) {
                child.setSoReuseaddr((Boolean) value);
            } else if (option == Option.SO_LINGER) {
                child.setSoLinger((Integer) value);
            } else {
                setOption0(child, option, value);
            }
        }

        return true;
    }

    private boolean setOption0(Netty netty, Option<?> option, Object value) {
        if (option == Option.CONNECT_TIMEOUT_MILLIS) {
            netty.setConnectTimeoutMillis((Integer) value);
        } else if (option == Option.WRITE_SPIN_COUNT) {
            netty.setWriteSpinCount((Integer) value);
        } else if (option == Option.ALLOCATOR) {
            netty.setAllocator((ByteBufAllocator) value);
        } else if (option == Option.RCVBUF_ALLOCATOR) {
            netty.setRcvbufAllocator((RecvByteBufAllocator) value);
        } else if (option == Option.AUTO_READ) {
            netty.setAutoRead((Boolean) value);
        } else if (option == Option.WRITE_BUFFER_WATER_MARK) {
            netty.setWriteBufferWaterMark((WriteBufferWaterMark) value);
        } else if (option == Option.MESSAGE_SIZE_ESTIMATOR) {
            netty.setMessageSizeEstimator((MessageSizeEstimator) value);
        } else if (option == Option.SINGLE_EVENTEXECUTOR_PER_GROUP) {
            netty.setSingleEventexecutorPerGroup((Boolean) value);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Config config() {
        return nettyConfig;
    }

    @Override
    public void shutdownGracefully() {
        bossGroup().shutdownGracefully();
        workerGroup().shutdownGracefully();
    }

    @Override
    public EventLoopGroup bossGroup() {
        return nettyConfig.bossGroup();
    }

    @Override
    public EventLoopGroup workerGroup() {
        return nettyConfig.workerGroup();
    }

    @Override
    public Class channel() {
        return nettyConfig.channel();
    }

    @Override
    public ChannelInitializer childHandler() {
        return nettyConfig.childHandler();
    }

    private void validate(INettyConfig INettyConfig) {
        if (INettyConfig == null) {
            throw new NullPointerException("nettyConfig == null");
        }
    }

    private void validate(SocketAddress localAddress, ServerBootstrap bootstrap) {
        if (localAddress == null) {
            throw new NullPointerException("localAddress == null");
        }
        validate(bootstrap);
    }

    private void validate(ServerBootstrap bootstrap) {
        if (bootstrap == null) {
            throw new NullPointerException("bootstrap == null");
        }
    }
}