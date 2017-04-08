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
    Channel bind(int port) throws InterruptedException;

    /**
     * Binds the {@link Channel}'s socket to a local address and configures the socket
     * to listen for connections.
     *
     * @param host
     * @param port
     */
    Channel bind(String host, int port) throws InterruptedException;

    /**
     * Binds the {@link Channel}'s socket to a local address and configures the socket
     * to listen for connections.
     *
     * @param localAddress
     */
    Channel bind(SocketAddress localAddress) throws InterruptedException;

    /**
     * Binds the rpc processor.
     */
    void processor(Processor processor);

    /**
     * Returns the {@link ChannelManager} of this {@link Acceptor}.
     */
    ChannelManager channelManager();

    /**
     * Shutdown the server gracefully.
     */
    void shutdownGracefully();
}
