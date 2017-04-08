package org.pudding.transport.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelManager;
import org.pudding.transport.api.DefaultChannelManager;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * Abstract acceptor based on Netty.
 *
 * @author Yohann.
 */
public abstract class NettyAcceptor implements Acceptor {

    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() << 2;

    protected ChannelManager channelManager = new DefaultChannelManager();

    protected SocketAddress localAddress;
    protected ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private int nWorkers;

    public NettyAcceptor() {
        this(DEFAULT_THREADS);
    }

    public NettyAcceptor(int nWorkers) {
        validate(nWorkers);
        this.nWorkers = nWorkers;
    }

    protected void init() {
        bootstrap = new ServerBootstrap();

        ThreadFactory bossFacetory = new DefaultThreadFactory("acceptor-boss", Thread.MAX_PRIORITY);
        ThreadFactory workerFacetory = new DefaultThreadFactory("acceptor-worker", Thread.MAX_PRIORITY);
        bossGroup = initEventLoopGroup(1, bossFacetory);
        workerGroup = initEventLoopGroup(nWorkers, workerFacetory);
    }

    protected abstract EventLoopGroup initEventLoopGroup(int nThreads, ThreadFactory factory);

    @Override
    public Channel bind(SocketAddress localAddress) throws InterruptedException {
        this.localAddress = localAddress;
        return null;
    }

    protected EventLoopGroup bossGroup() {
        return bossGroup;
    }

    protected EventLoopGroup workerGroup() {
        return workerGroup;
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
