package org.pudding.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * Abstract connector based on Netty.
 *
 * @author Yohann.
 */
public abstract class NettyConnector implements Connector {
    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() << 2;

    protected HashedWheelTimer timer = new HashedWheelTimer();

    protected final Bootstrap bootstrap;
    private final int nWorkers;

    private EventLoopGroup group;

    public NettyConnector() {
        this(DEFAULT_THREADS);
    }

    public NettyConnector(int nWorkers) {
        validate(nWorkers);
        this.nWorkers = nWorkers;
        bootstrap = new Bootstrap();
    }

    protected void init() {
        ThreadFactory facetory = new DefaultThreadFactory("connector", Thread.MAX_PRIORITY);

        group = initEventLoopGroup(nWorkers, facetory);
        bootstrap.group(group);

        doInit();
    }

    protected abstract void doInit();

    protected abstract EventLoopGroup initEventLoopGroup(int nThreads, ThreadFactory factory);

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
