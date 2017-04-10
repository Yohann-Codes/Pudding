package org.pudding.transport.api;

/**
 * Channel Factory.
 *
 * @author Yohann.
 */
public interface ChannelFactory<T> {

    /**
     * Create a instance of {@link Channel}.
     *
     * @param param
     * @return
     */
    Channel newInstance(T param);
}
