package org.pudding.registry;

import org.pudding.common.utils.Maps;
import org.pudding.transport.api.Channel;

import java.util.concurrent.ConcurrentMap;

/**
 * Hold channel that has subscribed.
 *
 * @author Yohann.
 */
public class ServiceSubContainer {

    private ConcurrentMap<String, Channel> channels = Maps.newConcurrentHashMap();

    public void put(String serviceName, Channel channel) {
        channels.put(serviceName, channel);
    }

    public Channel get(String serviceName) {
        if (channels.containsKey(serviceName)) {
            return channels.get(serviceName);
        }
        return null;
    }

    @Override
    public String toString() {
        return "ServiceSubContainer: " + channels;
    }
}
