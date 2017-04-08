package org.pudding.transport.api;

/**
 * Manage {@link Channel} that has established connection.
 *
 * @author Yohann.
 */
public interface ChannelManager {

    /**
     * Put a {@link Channel} to the pool.
     *
     * @param address host:port   e.g. 127.0.0.1:20000
     * @param channel
     * @return return true if success, or false.
     */
    boolean putChannel(String address, Channel channel);

    /**
     * Return the {@link Channel} of given address.
     *
     * @param address
     */
    Channel getChannel(String address);

    /**
     * Abandon the {@link Channel} of given address.
     *
     * @param address
     */
    Channel removeChannel(String address);

    /**
     * Setup the capacity of pool.
     *
     * @param capacity
     */
    void capacity(int capacity);

    /**
     * Return the capacity of pool.
     */
    int capacity();

    /**
     * Return the number of {@link Channel}.
     */
    int size();
}
