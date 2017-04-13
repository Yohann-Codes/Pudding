package org.pudding.transport.netty;

import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelFactory;

/**
 * {@link Channel}'s Factory based on Netty.
 *
 * @author Yohann.
 */
public class NettyChannelFactory implements ChannelFactory<io.netty.channel.Channel> {

    public static final ChannelFactory FACTORY_INSTANCE = new NettyChannelFactory();

    @SuppressWarnings("unchecked")
    public static Channel newChannel(io.netty.channel.Channel channel) {
        return FACTORY_INSTANCE.newInstance(channel);
    }

    @Override
    public Channel newInstance(io.netty.channel.Channel channel) {
        return new NettyChannel(channel);
    }
}
