package org.pudding.rpc.consumer.router;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Maps;
import org.pudding.transport.api.Channel;

import java.util.concurrent.ConcurrentMap;

/**
 * key: serviceMeta
 * value: channel
 *
 * @author Yohann.
 */
public class RouteMap {
    private static final ConcurrentMap<String, Channel> channels = Maps.newConcurrentHashMap();

    public static void put(ServiceMeta meta, Channel channel) {
        channels.put(key(meta), channel);
    }

    public static void remove(ServiceMeta meta) {
        channels.remove(key(meta));
    }

    public static Channel get(ServiceMeta meta) {
        String key = key(meta);
        synchronized (channels) {
            if (channels.containsKey(key)) {
                return channels.get(key);
            }
        }
        return null;
    }

    private static String key(ServiceMeta meta) {
        return meta.getName() + "-" + meta.getAddress();
    }
}
