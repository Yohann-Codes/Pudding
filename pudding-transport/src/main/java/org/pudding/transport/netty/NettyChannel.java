package org.pudding.transport.netty;

import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelFuture;

import java.net.SocketAddress;

/**
 * The implementation of {@link Channel} based on Netty.
 *
 * @author Yohann.
 */
public class NettyChannel implements Channel {
    private io.netty.channel.Channel channel;

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
    public ChannelFuture write(Object msg) {
        io.netty.channel.ChannelFuture future = channel.writeAndFlush(msg);
        return new NettyChannelFuture(future);
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
