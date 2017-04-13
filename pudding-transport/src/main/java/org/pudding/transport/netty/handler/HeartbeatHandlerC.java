package org.pudding.transport.netty.handler;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import org.pudding.common.protocol.Message;
import org.pudding.common.utils.MessageFactory;

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

    private ChannelFuture heartbeat(ChannelHandlerContext ctx) {
        Message message = MessageFactory.newHeartbeatMessage();
        return ctx.writeAndFlush(message);
    }
}