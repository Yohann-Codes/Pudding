package org.pudding.transport.netty;

import io.netty.channel.ChannelHandler;

/**
 * Hold the handlers of client.
 *
 * @author Yohann.
 */
public interface ChannelHandlerHolder {

    /**
     * Return the handlers of client.
     */
    ChannelHandler[] handlers();
}
