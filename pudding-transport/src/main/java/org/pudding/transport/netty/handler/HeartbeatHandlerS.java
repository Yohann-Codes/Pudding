package org.pudding.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;

/**
 * Acceptor心跳检测Handler.
 * <p>
 *
 * @author Yohann.
 */
public class HeartbeatHandlerS extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(HeartbeatHandlerS.class);

    // 丢失的心跳数
    private int count = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 心跳丢失
            count++;
            if (count > 4) {
                // 心跳丢失数达到5个，主动断开连接
                ctx.channel().close();
            }
            logger.info("心跳丢失: " + count);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageHolder) {
            MessageHolder holder = (MessageHolder) msg;
            ProtocolHeader header = holder.getHeader();
            byte packetCode = ProtocolHeader.dataPacketCode(header.getType());
            if (packetCode == ProtocolHeader.HEATBEAT) {
                // 心跳丢失清零
                count = 0;
//                logger.info("收到心跳包 " + ctx.channel());
                ReferenceCountUtil.release(msg);
            } else {
                ctx.fireChannelRead(msg);
            }
        } else {
            logger.warn("Unexpected msg type received: " + msg.getClass());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}