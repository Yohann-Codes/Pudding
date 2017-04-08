package org.pudding.transport.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelListener;

import java.net.SocketAddress;

/**
 * The implementation of {@link Channel} based on Netty.
 *
 * @author Yohann.
 */
public class NettyChannel implements Channel {
    private final io.netty.channel.Channel channel;

    public NettyChannel(io.netty.channel.Channel channel) {
        this.channel = channel;
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
    public void close() {
        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
