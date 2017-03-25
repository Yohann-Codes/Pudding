package org.pudding.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

/**
 * 异常处理Handler.
 *
 * @author Yohann.
 */
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("ExceptionHandler", cause);
    }
}
