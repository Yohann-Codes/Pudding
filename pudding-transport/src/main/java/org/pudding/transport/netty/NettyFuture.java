package org.pudding.transport.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Future;
import org.pudding.transport.api.FutureListener;

/**
 * 基于Netty的Future实现.
 *
 * @author Yohann.
 */
public class NettyFuture implements Future {
    private ChannelFuture future;

    public NettyFuture(ChannelFuture future) {
        this.future = future;
    }

    @Override
    public Channel channel() {
        return new NettyChannel(future.channel());
    }

    @Override
    public void addListener(final FutureListener listener) {
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                listener.operationComplete(future.isSuccess());
            }
        });
    }
}
