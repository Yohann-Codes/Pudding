package org.pudding.transport.api;

import org.pudding.transport.netty.ChildOption;

import java.net.SocketAddress;

/**
 * The client side.
 *
 * @author Yohann.
 */
public interface Connector {

    /**
     * Returns the remote address where this channel is connect to.
     */
    SocketAddress remoteAddress();

    /**
     * Connects the {@link Channel}'s socket to a remote address.
     *
     * @param host
     * @param port
     */
    ChannelFuture connect(String host, int port) throws InterruptedException;

    /**
     * Connects the {@link Channel}'s socket to a remote address.
     *
     * @param remoteAddress
     * @return if failed, return null.
     */
    ChannelFuture connect(SocketAddress remoteAddress) throws InterruptedException;

    /**
     * Returns the option, that is {@link ChildOption} of this {@link Connector}'s instance.
     */
    OptionConfig option();

    /**
     * Shutdown the client gracefully;
     */
    void shutdownGracefully();
}
