package org.pudding.transport.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.OptionConfig;

import static org.pudding.transport.api.Option.*;

/**
 * Abstract Acceptor based on Netty.
 *
 * @author Yohann.
 */
public abstract class AbstractNettyAcceptor implements Acceptor {

    protected final ServerBootstrap bootstrap = new ServerBootstrap();
    protected final AcceptorNettyConfig config;

    public AbstractNettyAcceptor(AcceptorNettyConfig config) {
        if (config == null) {
            ChannelInitializer<SocketChannel> initializer = initInitializer();
            config = newAcceptorNettyConfig(initializer);
        }
        this.config = config;
    }

    protected abstract ChannelInitializer<SocketChannel> initInitializer();

    protected abstract AcceptorNettyConfig newAcceptorNettyConfig(ChannelInitializer<SocketChannel> initializer);

    protected void setParentOptions(OptionConfig parentOption) {
        bootstrap.option(ChannelOption.AUTO_READ, parentOption.getOption(AUTO_READ));
        bootstrap.option(ChannelOption.AUTO_CLOSE, parentOption.getOption(AUTO_CLOSE));
        bootstrap.option(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, parentOption.getOption(SINGLE_EVENTEXECUTOR_PER_GROUP));
        if (parentOption.getOption(CONNECT_TIMEOUT_MILLIS) > 0) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, parentOption.getOption(CONNECT_TIMEOUT_MILLIS));
        }
        if (parentOption.getOption(MAX_MESSAGES_PER_READ) > 0) {
            bootstrap.option(ChannelOption.MAX_MESSAGES_PER_READ, parentOption.getOption(MAX_MESSAGES_PER_READ));
        }
        if (parentOption.getOption(WRITE_SPIN_COUNT) > 0) {
            bootstrap.option(ChannelOption.WRITE_SPIN_COUNT, parentOption.getOption(WRITE_SPIN_COUNT));
        }
        if (parentOption.getOption(WRITE_BUFFER_HIGH_WATER_MARK) > 0) {
            bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, parentOption.getOption(WRITE_BUFFER_HIGH_WATER_MARK));
        }
        if (parentOption.getOption(WRITE_BUFFER_LOW_WATER_MARK) > 0) {
            bootstrap.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, parentOption.getOption(WRITE_BUFFER_LOW_WATER_MARK));
        }
        if (parentOption.getOption(ALLOCATOR) != null) {
            bootstrap.option(ChannelOption.ALLOCATOR, parentOption.getOption(ALLOCATOR));
        }
        if (parentOption.getOption(RCVBUF_ALLOCATOR) != null) {
            bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, parentOption.getOption(RCVBUF_ALLOCATOR));
        }
        if (parentOption.getOption(WRITE_BUFFER_WATER_MARK) != null) {
            bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, parentOption.getOption(WRITE_BUFFER_WATER_MARK));
        }
        if (parentOption.getOption(MESSAGE_SIZE_ESTIMATOR) != null) {
            bootstrap.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, parentOption.getOption(MESSAGE_SIZE_ESTIMATOR));
        }
    }

    protected void setChildOptions(OptionConfig childOption) {
        bootstrap.childOption(ChannelOption.AUTO_READ, childOption.getOption(AUTO_READ));
        bootstrap.childOption(ChannelOption.AUTO_CLOSE, childOption.getOption(AUTO_CLOSE));
        bootstrap.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, childOption.getOption(SINGLE_EVENTEXECUTOR_PER_GROUP));
        if (childOption.getOption(CONNECT_TIMEOUT_MILLIS) > 0) {
            bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, childOption.getOption(CONNECT_TIMEOUT_MILLIS));
        }
        if (childOption.getOption(MAX_MESSAGES_PER_READ) > 0) {
            bootstrap.childOption(ChannelOption.MAX_MESSAGES_PER_READ, childOption.getOption(MAX_MESSAGES_PER_READ));
        }
        if (childOption.getOption(WRITE_SPIN_COUNT) > 0) {
            bootstrap.childOption(ChannelOption.WRITE_SPIN_COUNT, childOption.getOption(WRITE_SPIN_COUNT));
        }
        if (childOption.getOption(WRITE_BUFFER_HIGH_WATER_MARK) > 0) {
            bootstrap.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, childOption.getOption(WRITE_BUFFER_HIGH_WATER_MARK));
        }
        if (childOption.getOption(WRITE_BUFFER_LOW_WATER_MARK) > 0) {
            bootstrap.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, childOption.getOption(WRITE_BUFFER_LOW_WATER_MARK));
        }
        if (childOption.getOption(ALLOCATOR) != null) {
            bootstrap.childOption(ChannelOption.ALLOCATOR, childOption.getOption(ALLOCATOR));
        }
        if (childOption.getOption(RCVBUF_ALLOCATOR) != null) {
            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, childOption.getOption(RCVBUF_ALLOCATOR));
        }
        if (childOption.getOption(WRITE_BUFFER_WATER_MARK) != null) {
            bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, childOption.getOption(WRITE_BUFFER_WATER_MARK));
        }
        if (childOption.getOption(MESSAGE_SIZE_ESTIMATOR) != null) {
            bootstrap.childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, childOption.getOption(MESSAGE_SIZE_ESTIMATOR));
        }
    }
}
