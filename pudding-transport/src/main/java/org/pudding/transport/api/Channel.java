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
    ChannelFuture write(Object msg);

    /**
     * Request to close the {@link Channel}.
     */
    void close();
}
