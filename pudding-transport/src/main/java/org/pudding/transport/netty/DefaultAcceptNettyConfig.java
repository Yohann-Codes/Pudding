package org.pudding.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.pudding.transport.common.Option;

/**
 * 接收端Netty默认配置.
 *
 * @author Yohann.
 */
public class DefaultAcceptNettyConfig extends AcceptNettyConfig {

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
    }

    public DefaultAcceptNettyConfig() {
        super(channelClass, initializer);
        defaultOption();
    }

    private void defaultOption() {
        option(Option.SO_BACKLOG, 128);
        childOption(Option.SO_KEEPALIVE, true);
    }
}
