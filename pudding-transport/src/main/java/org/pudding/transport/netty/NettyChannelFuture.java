package org.pudding.transport.netty;

import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelFuture;
import org.pudding.transport.api.ChannelFutureListener;

/**
 * The implementation of {@link ChannelFuture} based on Netty.
 *
 * @author Yohann.
 */
public class NettyChannelFuture implements ChannelFuture {
    private io.netty.channel.ChannelFuture future;
    private io.netty.channel.Channel channel;

    public NettyChannelFuture(io.netty.channel.ChannelFuture future) {
        this.future = future;
        channel = future.channel();
    }

    @Override
    public Channel channel() {
        return new NettyChannel(channel);
    }

    @Override
    public void addListener(final ChannelFutureListener listener) {
        future.addListener(new io.netty.channel.ChannelFutureListener() {
            @Override
            public void operationComplete(io.netty.channel.ChannelFuture future) throws Exception {
                NettyChannel channel = new NettyChannel(NettyChannelFuture.this.channel);
                Throwable cause = future.cause();
                if (future.isSuccess()) {
                    listener.operationSuccess(channel);
                } else {
                    listener.operationFailure(channel,cause );
                }
            }
        });
    }
}
