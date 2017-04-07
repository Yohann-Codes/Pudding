package org.pudding.transport.api;

import java.net.SocketAddress;

/**
 * The server side.
 *
 * @author Yohann.
 */
public interface Acceptor {

    /**
     * Returns the local address where this channel is bound to.
     */
    SocketAddress localAddress();

    /**
     * Binds the {@link Channel}'s socket to a local address and configures the socket
     * to listen for connections.
     *
     * @param port
     */
    ChannelFuture bind(int port) throws InterruptedException;

    /**
     * Binds the {@link Channel}'s socket to a local address and configures the socket
     * to listen for connections.
     *
     * @param host
     * @param port
     */
    ChannelFuture bind(String host, int port) throws InterruptedException;

    /**
     * Binds the {@link Channel}'s socket to a local address and configures the socket
     * to listen for connections.
     *
     * @param localAddress
     */
    ChannelFuture bind(SocketAddress localAddress) throws InterruptedException;

    /**
     * Returns the {@link OptionGroup}'s instance of this {@link Acceptor}.
     */
    OptionGroup optionGroup();

    /**
     * Shutdown the server gracefully.
     */
    void shutdownGracefully();
}
