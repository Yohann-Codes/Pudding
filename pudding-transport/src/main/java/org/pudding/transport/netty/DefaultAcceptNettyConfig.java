package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.handler.*;

/**
 * 接收端Netty默认配置.
 *
 * @author Yohann.
 */
public class DefaultAcceptNettyConfig extends AcceptNettyConfig {

    private static ProtocolDecoder decoder;
    private static ProtocolEncoder encoder;
    private static IdleStateHandler idleStateHandler;
    private static HeartbeatHandlerS heartbeatHandler;
    private static AcceptorHandler acceptorHandler;
    private static ExceptionHandler exceptionHandler;

    static {
        decoder = new ProtocolDecoder();
        encoder = new ProtocolEncoder();
        idleStateHandler = new IdleStateHandler(6, 0, 0);
        heartbeatHandler = new HeartbeatHandlerS();
        acceptorHandler = new AcceptorHandler();
        exceptionHandler = new ExceptionHandler();
    }

    private static Class<? extends ServerChannel> channelClass = NioServerSocketChannel.class;

    private static ChannelInitializer initializer = new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            addDefaultHandlers(ch);
        }
    };

    private static void addDefaultHandlers(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        // Add handler
        pipeline.addLast(decoder, encoder, idleStateHandler,
                heartbeatHandler, acceptorHandler, exceptionHandler);
    }

    public DefaultAcceptNettyConfig(Processor processor) {
        super(channelClass, initializer);
        acceptorHandler.processor(processor);
        defaultOption();
    }

    private void defaultOption() {
        option(Option.SO_BACKLOG, 128);
        childOption(Option.SO_KEEPALIVE, true);
    }
}
