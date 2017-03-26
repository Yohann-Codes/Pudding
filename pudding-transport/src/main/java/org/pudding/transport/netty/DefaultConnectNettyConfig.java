package org.pudding.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.handler.*;

/**
 * 连接端Netty默认配置.
 *
 * @author Yohann.
 */
public class DefaultConnectNettyConfig extends ConnectNettyConfig {

    private static ProtocolDecoder decoder;
    private static ProtocolEncoder encoder;
    private static IdleStateHandler idleStateHandler;
    private static HeartbeatHandlerC heartbeatHandler;
    private static AcceptorHandler acceptorHandler;
    private static ExceptionHandler exceptionHandler;

    static {
        decoder = new ProtocolDecoder();
        encoder = new ProtocolEncoder();
        idleStateHandler = new IdleStateHandler(0, 5, 0);
        heartbeatHandler = new HeartbeatHandlerC();
        acceptorHandler = new AcceptorHandler();
        exceptionHandler = new ExceptionHandler();
    }

    private static Class<? extends Channel> channelClass = NioSocketChannel.class;

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

    public DefaultConnectNettyConfig(Processor processor) {
        super(channelClass, initializer);
        processor(acceptorHandler, processor);
        defaultOption();
    }

    private void defaultOption() {
        option(Option.SO_KEEPALIVE, true);
    }
}
