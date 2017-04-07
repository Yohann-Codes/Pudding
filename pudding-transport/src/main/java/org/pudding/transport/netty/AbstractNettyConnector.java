package org.pudding.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.OptionConfig;

import static org.pudding.transport.api.Option.*;
import static org.pudding.transport.api.Option.MESSAGE_SIZE_ESTIMATOR;

/**
 * Abstract connector based on Netty.
 *
 * @author Yohann.
 */
public abstract class AbstractNettyConnector implements Connector {

    protected final Bootstrap bootstrap = new Bootstrap();
    protected final ConnectorNettyConfig config;

    public AbstractNettyConnector(ConnectorNettyConfig config) {
        if (config == null) {
            ChannelInitializer<SocketChannel> initializer = initInitializer();
            config = newConnectorNettyConfig(initializer);
        }
        this.config = config;
    }

    protected abstract ChannelInitializer<SocketChannel> initInitializer();

    protected abstract ConnectorNettyConfig newConnectorNettyConfig(ChannelInitializer<SocketChannel> initializer);

    protected void setOptions0(OptionConfig option) {
        bootstrap.option(ChannelOption.AUTO_READ, option.getOption(AUTO_READ));
        bootstrap.option(ChannelOption.AUTO_CLOSE, option.getOption(AUTO_CLOSE));
        bootstrap.option(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, option.getOption(SINGLE_EVENTEXECUTOR_PER_GROUP));
        if (option.getOption(CONNECT_TIMEOUT_MILLIS) > 0) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, option.getOption(CONNECT_TIMEOUT_MILLIS));
        }
        if (option.getOption(MAX_MESSAGES_PER_READ) > 0) {
            bootstrap.option(ChannelOption.MAX_MESSAGES_PER_READ, option.getOption(MAX_MESSAGES_PER_READ));
        }
        if (option.getOption(WRITE_SPIN_COUNT) > 0) {
            bootstrap.option(ChannelOption.WRITE_SPIN_COUNT, option.getOption(WRITE_SPIN_COUNT));
        }
        if (option.getOption(WRITE_BUFFER_HIGH_WATER_MARK) > 0) {
            bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, option.getOption(WRITE_BUFFER_HIGH_WATER_MARK));
        }
        if (option.getOption(WRITE_BUFFER_LOW_WATER_MARK) > 0) {
            bootstrap.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, option.getOption(WRITE_BUFFER_LOW_WATER_MARK));
        }
        if (option.getOption(ALLOCATOR) != null) {
            bootstrap.option(ChannelOption.ALLOCATOR, option.getOption(ALLOCATOR));
        }
        if (option.getOption(RCVBUF_ALLOCATOR) != null) {
            bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, option.getOption(RCVBUF_ALLOCATOR));
        }
        if (option.getOption(WRITE_BUFFER_WATER_MARK) != null) {
            bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, option.getOption(WRITE_BUFFER_WATER_MARK));
        }
        if (option.getOption(MESSAGE_SIZE_ESTIMATOR) != null) {
            bootstrap.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, option.getOption(MESSAGE_SIZE_ESTIMATOR));
        }
    }
}
