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

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Watch the connection.
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

    private boolean isMultiAddress;

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, SocketAddress remoteAddress) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        singleAddress = remoteAddress;
        realAddress = remoteAddress;
        isMultiAddress = false;
    }

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, SocketAddress realAddress, SocketAddress[] remoteAddress) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.realAddress = realAddress;
        multiAddress = remoteAddress;
        isMultiAddress = true;
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
                return;
            }
            long timeout = 2 << attempts;
            timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
        }

        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        if (isMultiAddress) {
            // multiple address pattern
            int size = multiAddress.length;
            if (size < 2) {
                realAddress = multiAddress[0];
            } else {
                // Select an address randomly
                int index = RandomUtil.getInt(size);
                realAddress = multiAddress[index];
            }
        } else {
            // single address pattern
            realAddress = singleAddress;
        }

        ChannelFuture future;
        synchronized (bootstrap) {
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
