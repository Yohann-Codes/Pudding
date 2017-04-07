package org.pudding.transport.api;

/**
 * The result of an asynchronous {@link Channel} I/O operation.
 *
 * @author Yohann.
 */
public interface ChannelFuture {

    /**
     * Return the {@link Channel} of this {@link ChannelFuture}.
     */
    Channel channel();

    /**
     * Add the specified listener to this future.
     */
    void addListener(ChannelFutureListener listener);
}
