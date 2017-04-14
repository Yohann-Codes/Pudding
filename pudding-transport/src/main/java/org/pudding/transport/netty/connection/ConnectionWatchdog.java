package org.pudding.transport.netty.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.apache.log4j.Logger;
import org.pudding.common.utils.RandomUtil;
import org.pudding.transport.netty.ChannelHandlerHolder;
import org.pudding.transport.netty.NettyTcpConnector;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Reconnection.
 * 1) Single address.
 * 2) Multiple address.
 *
 * @author Yohann.
 */
@ChannelHandler.Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements ChannelHandlerHolder, TimerTask {
    private static final Logger logger = Logger.getLogger(ConnectionWatchdog.class);

    private final Bootstrap bootstrap;
    private final Timer timer;

    private SocketAddress singleAddress; // single address
    private SocketAddress[] multiAddress; // multiple address
    private SocketAddress realAddress;

    public static final int ST_OPEN = 1;  // Open automatic reconnection
    public static final int ST_CLOSE = 2; // Close automatic reconnection

    private volatile int state;
    private int attempts;

    // The number of crash server
    private volatile int crashNumber;

    // The reconnection pattern
    private NettyTcpConnector.ReconnPattern reconnPattern;

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, SocketAddress remoteAddress,
                              NettyTcpConnector.ReconnPattern reconnPattern) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        singleAddress = remoteAddress;
        realAddress = remoteAddress;
        this.reconnPattern = reconnPattern;
    }

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, SocketAddress realAddress,
                              SocketAddress[] remoteAddress, NettyTcpConnector.ReconnPattern reconnPattern) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.realAddress = realAddress;
        multiAddress = remoteAddress;
        this.reconnPattern = reconnPattern;
    }

    public void openAutoReconnection() {
        state = ST_OPEN;
    }

    public void closeAutoReconnection() {
        state = ST_CLOSE;
    }

    public int autoReconnectionState() {
        return state;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        attempts = 0;
        logger.info("connect to " + realAddress);

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("disconnect to " + realAddress);

        if (autoReconnectionState() == ST_OPEN) {
            if (attempts < 12) {
                attempts++;
                logger.info("try to reconnect to " + realAddress);
            } else {
                logger.warn("stop trying reconnect to " + realAddress);

                ctx.fireChannelInactive();
                return;
            }
            long timeout = 2 << attempts;
            timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
        }

        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        ChannelFuture future;
        synchronized (bootstrap) {
            if (reconnPattern == NettyTcpConnector.ReconnPattern.CONNECT_OLD_ADDRESS) {
                realAddress = singleAddress;
            }
            if (reconnPattern == NettyTcpConnector.ReconnPattern.CONNECT_RANDOM_ADDRESS) {
                int size = multiAddress.length;
                if (size < 2) {
                    realAddress = multiAddress[0];
                } else {
                    // Select an address randomly
                    int index = RandomUtil.getInt(size);
                    realAddress = multiAddress[index];
                }
            }
            if (reconnPattern == NettyTcpConnector.ReconnPattern.CONNECT_PREVIOUS_ADDRESS) {
                crashNumber++;
                int addrIndex = multiAddress.length - 1 - crashNumber;
                if (addrIndex < 0) {
                    logger.warn("all the other registry servers have crashed");
                    return;
                }
                realAddress = multiAddress[addrIndex];
            }

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(realAddress);
        }

        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                boolean succeed = f.isSuccess();

                if (!succeed) {
                    f.channel().pipeline().fireChannelInactive();
                }
            }
        });
    }
}
