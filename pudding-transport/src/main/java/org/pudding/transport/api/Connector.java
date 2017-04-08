package org.pudding.transport.api;

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
    Channel connect(String host, int port) throws InterruptedException;

    /**
     * Connects the {@link Channel}'s socket to a remote address.
     *
     * @param remoteAddress
     * @return if failed, return null.
     */
    Channel connect(SocketAddress remoteAddress) throws InterruptedException;

    /**
     * Binds the rpc processor.
     */
    void processor(Processor processor);

    /**
     * Returns the {@link ChannelManager} of this {@link Connector}.
     */
    ChannelManager channelManager();

    /**
     * Shutdown the client gracefully;
     */
    void shutdownGracefully();
}
