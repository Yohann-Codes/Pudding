package org.pudding.transport.netty.handler;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import org.pudding.common.utils.SequenceUtil;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.protocol.ProtocolHeader;

/**
 * The heatbeat handler of connector.
 * <p>
 *
 * @author Yohann.
 */
@ChannelHandler.Sharable
public class HeartbeatHandlerC extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(HeartbeatHandlerC.class);

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if ((state == IdleState.WRITER_IDLE)) {
                // Send heatbeat
                heartbeat(ctx).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            heartbeat(ctx);
                        }
                    }
                });
            }
        }
    }

    /**
     * Create and send heatbeat.
     *
     * @param ctx
     */
    private ChannelFuture heartbeat(ChannelHandlerContext ctx) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC)
                // The heatbeat can ignore serialization type
                .setType(ProtocolHeader.type((byte) 0, ProtocolHeader.HEATBEAT))
                .setSign((byte) 0)
                .setSequence(SequenceUtil.generateSequence())
                .setStatus(0)
                .setBodyLength(0);

        Message message = new Message();
        message.setHeader(header)
                .setBody(new byte[0]);

        return ctx.writeAndFlush(message);
    }
}