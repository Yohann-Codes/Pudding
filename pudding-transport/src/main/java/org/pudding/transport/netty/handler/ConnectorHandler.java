package org.pudding.transport.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.pudding.common.exception.ProcessorIsNullException;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.utils.AddressUtil;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelManager;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * After decoding the message.
 *
 * @author Yohann.
 */
@ChannelHandler.Sharable
public class ConnectorHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ConnectorHandler.class);

    private Processor processor;
    private ChannelManager channelManager;

    public ConnectorHandler(Connector connector) {
        channelManager = connector.channelManager();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageHolder) {
            Channel channel = new NettyChannel(ctx.channel());
            processor.handleMessage(channel, (MessageHolder) msg);
        } else {
            logger.warn("unexpected msg type received: " + msg.getClass());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) remoteAddress;
            String host = address.getAddress().getHostAddress();
            int port = address.getPort();
            channelManager.putChannel(AddressUtil.address(host, port), new NettyChannel(ctx.channel()));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) remoteAddress;
            String host = address.getAddress().getHostAddress();
            int port = address.getPort();
            channelManager.removeChannel(AddressUtil.address(host, port));
        }
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }
}
