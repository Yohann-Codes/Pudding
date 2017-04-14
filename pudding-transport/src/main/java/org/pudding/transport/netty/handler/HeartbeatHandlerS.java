package org.pudding.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.protocol.ProtocolHeader;

/**
 * The heatbeat handler of Acceptor.
 * <p>
 *
 * @author Yohann.
 */
public class HeartbeatHandlerS extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(HeartbeatHandlerS.class);

    // The number of heatbeat failure
    private int count = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // heatbeat failture  +1
                count++;
                if (count > 4) {
                    // Close the channel
                    ctx.channel().close();
                }
                logger.info("heatbeat failure: " + count);
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message) {
            Message holder = (Message) msg;
            ProtocolHeader header = holder.getHeader();
            byte messageType = ProtocolHeader.messageType(header.getType());
            if (messageType == ProtocolHeader.HEATBEAT) {
                // Clear the number of heatbeat failure
                count = 0;
//                logger.info(ctx.channel() + " heatbeat");
                ReferenceCountUtil.release(msg);
            } else {
                ctx.fireChannelRead(msg);
            }
        } else {
            logger.warn("unexpected msg type received: " + msg.getClass());
            ReferenceCountUtil.release(msg);
        }
    }
}