package org.pudding.transport.api;

import java.net.SocketAddress;

/**
 * Pudding Channel.
 *
 * @author Yohann.
 */
public interface Channel {

    /**
     * Returns the local address where this channel is bound to.
     */
    SocketAddress localAddress();

    /**
     * Returns the remote address where this channel is connected to.
     */
    SocketAddress remoteAddress();

    /**
     * Return {@code true} if the {@link Channel} is active and so connected.
     */
    boolean isActive();

    /**
     * Request write and flush all pending messages.
     */
    Channel write(Object msg);

    /**
     * Request write and flush all pending messages with {@link ChannelListener}.
     */
    Channel write(Object msg, ChannelListener listener);

    /**
     * Open the automatic reconnection of current {@link Channel}.
     */
    void openAutoReconnection();

    /**
     * Close the automatic reconnection of current {@link Channel}.
     */
    void closeAutoReconnection();

    /**
     * Return ture if the {@link Channel} has opened the automatic reconnection, or false.
     */
    boolean isOpenAutoReconnection();

    /**
     * Request to close the {@link Channel}.
     */
    void close();
}
