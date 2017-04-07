package org.pudding.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.pudding.common.exception.ProcessorIsNullException;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyChannel;

/**
 * After decoding the message.
 *
 * @author Yohann.
 */
public class ConnectorHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ConnectorHandler.class);

    private Processor processor;

    public ConnectorHandler(Processor processor) {
        this.processor = processor;
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
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }
}
