package org.pudding.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelManager;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.DefaultChannelManager;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * Abstract connector based on Netty.
 *
 * @author Yohann.
 */
public abstract class NettyConnector implements Connector {
    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() << 2;

    protected ChannelManager channelManager = new DefaultChannelManager();

    protected HashedWheelTimer timer = new HashedWheelTimer();

    protected SocketAddress remoteAddress;
    protected Bootstrap bootstrap;

    private EventLoopGroup group;

    private int nWorkers;

    public NettyConnector() {
        this(DEFAULT_THREADS);
    }

    public NettyConnector(int nWorkers) {
        validate(nWorkers);
        this.nWorkers = nWorkers;
    }

    protected void init() {
        bootstrap = new Bootstrap();

        ThreadFactory facetory = new DefaultThreadFactory("connector", Thread.MAX_PRIORITY);
        group = initEventLoopGroup(nWorkers, facetory);
    }

    protected abstract EventLoopGroup initEventLoopGroup(int nThreads, ThreadFactory factory);

    @Override
    public Channel connect(SocketAddress remoteAddress) throws InterruptedException {
        this.remoteAddress = remoteAddress;
        return null;
    }

    protected EventLoopGroup group() {
        return group;
    }

    protected  <T> void checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
    }

    private void validate(int nWorkers) {
        if (nWorkers < 1) {
            throw new IllegalArgumentException("nWorker: " + nWorkers);
        }
    }
}
