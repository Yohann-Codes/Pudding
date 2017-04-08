package org.pudding.transport.api;

import org.pudding.common.utils.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 * The default implementation of {@link ChannelManager}.
 *
 * @author Yohann.
 */
public class DefaultChannelManager implements ChannelManager {

    private static final int DEFAULT_CAPACITY = 20000;

    private ConcurrentMap<String, Channel> channels = Maps.newConcurrentHashMap();

    private volatile int capacity = DEFAULT_CAPACITY;

    @Override
    public boolean putChannel(String address, Channel channel) {
        if (channels.size() > capacity) {
            return false;
        }
        channels.put(address, channel);
        return true;
    }

    @Override
    public Channel getChannel(String address) {
        return channels.get(address);
    }

    @Override
    public Channel removeChannel(String address) {
        return channels.remove(address);
    }

    @Override
    public void capacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int size() {
        return channels.size();
    }
}
