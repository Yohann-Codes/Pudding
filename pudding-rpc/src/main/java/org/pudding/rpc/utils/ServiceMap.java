package org.pudding.rpc.utils;

import org.pudding.common.model.ServiceMeta;
import org.pudding.transport.api.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * 每个服务对应一个Channel.
 *
 * @author Yohann.
 */
public class ServiceMap {
    private Map<String, Channel> services;

    public ServiceMap() {
        services = new HashMap<>();
    }

    public void put(ServiceMeta serviceMeta, Channel channel) {
        services.put(key(serviceMeta), channel);
    }

    public Channel get(ServiceMeta serviceMeta) {
        return services.get(key(serviceMeta));
    }

    public boolean containsKey(ServiceMeta serviceMeta) {
        return services.containsKey(key(serviceMeta));
    }

    public void remove(ServiceMeta serviceMeta) {
        services.remove(key(serviceMeta));
    }

    public int size() {
        return services.size();
    }

    private String key(ServiceMeta serviceMeta) {
        String name = serviceMeta.getName();
        String address = serviceMeta.getAddress();
        return name + " " + address;
    }

    @Override
    public String toString() {
        return "ServiceMap{" +
                "services=" + services +
                '}';
    }
}
