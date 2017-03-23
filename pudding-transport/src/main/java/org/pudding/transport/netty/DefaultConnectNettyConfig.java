package org.pudding.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.pudding.transport.options.Option;

/**
 * 连接端Netty默认配置.
 *
 * @author Yohann.
 */
public class DefaultConnectNettyConfig extends ConnectNettyConfig {

    private static Class<? extends Channel> channelClass = NioSocketChannel.class;

    private static ChannelInitializer initializer = new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            addDefaultHandlers(ch);
        }
    };

    private static void addDefaultHandlers(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        // Add handlers
    }

    public DefaultConnectNettyConfig() {
        super(channelClass, initializer);
        defaultOption();
    }

    private void defaultOption() {
        option(Option.SO_KEEPALIVE, true);
    }
}
