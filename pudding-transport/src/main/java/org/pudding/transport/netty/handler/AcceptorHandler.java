package org.pudding.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.transport.api.ProcessorHandler;
import org.pudding.common.MessageHolder;
import org.pudding.transport.exception.ProcessorIsNullException;
import org.pudding.transport.netty.NettyChannel;

/**
 * 消息接收Handler.
 *
 * @author Yohann.
 */
public class AcceptorHandler extends ChannelInboundHandlerAdapter implements ProcessorHandler {
    private static final Logger logger = Logger.getLogger(AcceptorHandler.class);

    private Processor processor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageHolder) {
            validate(processor);
            Channel channel = new NettyChannel(ctx.channel());
            processor.channelRead(channel, (MessageHolder) msg);
        } else {
            logger.warn("Unexpected msg type received: " + msg.getClass());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        validate(processor);
        processor.channelActive(new NettyChannel(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        validate(processor);
        processor.channelInactive(new NettyChannel(ctx.channel()));
    }

    private void validate(Processor processor) {
        if (processor == null) {
            throw new ProcessorIsNullException("processor == null");
        }
    }

    @Override
    public void processor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
