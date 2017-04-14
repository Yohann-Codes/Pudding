package org.pudding.transport.api;

import org.pudding.transport.protocol.Message;

/**
 * The processor that will be implemented by other module.
 *
 * @author Yohann.
 */
public interface Processor {

    /**
     * Handle network message.
     *
     * @param channel
     * @param holder
     */
    void handleMessage(Channel channel, Message holder);

    /**
     * Connect with ...
     *
     * @param channel
     */
    void handleConnection(Channel channel);

    /**
     * Disconnect with...
     *
     * @param channel
     */
    void handleDisconnection(Channel channel);
}
