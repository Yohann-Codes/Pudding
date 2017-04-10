package org.pudding.transport.netty;

import io.netty.channel.ChannelFuture;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelFactory;

/**
 * {@link Channel}'s Factory based on Netty.
 *
 * @author Yohann.
 */
public class NettyChannelFactory implements ChannelFactory<ChannelFuture> {

    public static final ChannelFactory FACTORY_INSTANCE = new NettyChannelFactory();

    @SuppressWarnings("unchecked")
    public static Channel newChannel(ChannelFuture future) {
        return FACTORY_INSTANCE.newInstance(future);
    }

    @Override
    public Channel newInstance(ChannelFuture future) {
        return new NettyChannel(future.channel());
    }
}
