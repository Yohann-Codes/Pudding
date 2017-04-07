package org.pudding.transport.netty.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.utils.MessageHolderFactory;

/**
 * The heatbeat handler of connector.
 * <p>
 *
 * @author Yohann.
 */
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
        MessageHolder holder = MessageHolderFactory.newHeartbeatHolder();
        return ctx.writeAndFlush(holder);
    }
}