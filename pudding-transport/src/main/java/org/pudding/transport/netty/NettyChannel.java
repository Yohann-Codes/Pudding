package org.pudding.transport.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import org.apache.log4j.Logger;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelListener;
import org.pudding.transport.netty.connection.ConnectionWatchdog;

import java.net.SocketAddress;

/**
 * The implementation of {@link Channel} based on Netty.
 *
 * @author Yohann.
 */
public class NettyChannel implements Channel {
    private static final Logger logger = Logger.getLogger(NettyChannel.class);

    private final io.netty.channel.Channel channel;
    private final ChannelPipeline pipeline;

    public NettyChannel(io.netty.channel.Channel channel) {
        this.channel = channel;
        pipeline = channel.pipeline();
    }

    @Override
    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public Channel write(Object msg) {
        channel.writeAndFlush(msg);
        return new NettyChannel(channel);
    }

    @Override
    public Channel write(Object msg, final ChannelListener listener) {
        final NettyChannel channel = new NettyChannel(this.channel);
        this.channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    listener.operationSuccess(channel);
                } else {
                    listener.operationFailure(channel, future.cause());
                }
            }
        });
        return channel;
    }

    @Override
    public void openAutoReconnection() {
        getWatchdog().openAutoReconnection();
    }

    @Override
    public void closeAutoReconnection() {
        getWatchdog().closeAutoReconnection();
    }

    @Override
    public boolean isOpenAutoReconnection() {
        int state = getWatchdog().autoReconnectionState();
        boolean isOpen = false;
        if (state == ConnectionWatchdog.ST_OPEN) {
            isOpen = true;
        }
        return isOpen;
    }

    private ConnectionWatchdog getWatchdog() {
        return pipeline.get(ConnectionWatchdog.class);
    }

    @Override
    public void close() {
        try {
            getWatchdog().closeAutoReconnection();
            channel.close().sync();
            logger.info("close channel: " + channel);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public String toString() {
        return channel.toString();
    }
}
