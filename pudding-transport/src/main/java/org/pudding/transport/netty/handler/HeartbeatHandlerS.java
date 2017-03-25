package org.pudding.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

/**
 * Acceptor心跳检测Handler.
 * <p>
 *
 * @author Yohann.
 */
public class HeartbeatHandlerS extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(HeartbeatHandlerS.class);

    // 丢失的心跳数
    private int counter = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            // 心跳丢失
//            counter++;
//            if (counter > 4) {
//                // 心跳丢失数达到5个，主动断开连接
//                ctx.channel().close();
//            }
//        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HeartbeatHandlerS.channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HeartbeatHandlerS.channelInactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }
}