package org.pudding.transport.netty;

import io.netty.channel.ChannelFuture;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Future;

/**
 * 基于Netty的Channel实现.
 *
 * @author Yohann.
 */
public class NettyChannel implements Channel {
    private io.netty.channel.Channel channel;

    public NettyChannel(io.netty.channel.Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public Future write(Object msg) {
        ChannelFuture future = channel.writeAndFlush(msg);
        NettyFuture nettyFuture = new NettyFuture(future);
        return nettyFuture;
    }

    @Override
    public void close() {
        channel.close();
    }
}
