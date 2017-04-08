package org.pudding.transport.api;

/**
 * To monitor the {@link Channel}.
 *
 * @author Yohann.
 */
public interface ChannelListener {

    /**
     * Means that the operation is successful.
     *
     * @param channel
     */
    void operationSuccess(Channel channel);

    /**
     * Means that the operation is failed.
     *
     * @param channel
     * @param cause
     */
    void operationFailure(Channel channel, Throwable cause);
}
