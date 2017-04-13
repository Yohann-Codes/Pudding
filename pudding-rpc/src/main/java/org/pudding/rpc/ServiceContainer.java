package org.pudding.rpc;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Maps;
import org.pudding.common.utils.Sets;
import org.pudding.transport.api.Channel;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Service container.
 *
 * @author Yohann.
 */
public final class ServiceContainer {

    private final ConcurrentMap<String, Channel> services;
    private final Set<Entry> entrySet;

    public ServiceContainer() {
        services = Maps.newConcurrentHashMap();
        entrySet = Sets.newHashSet();
    }

    public void put(ServiceMeta serviceMeta, Channel channel) {
        synchronized (services) { // Enture atomicity
            if (containsService(serviceMeta)) {
                throw new IllegalStateException("service was put many times, serviceMeta: " + serviceMeta);
            }
            services.put(key(serviceMeta), channel);
            entrySet.add(new Entry(serviceMeta, channel));
        }
    }

    public void remove(ServiceMeta serviceMeta) {
        synchronized (services) { // Enture atomicity
            if (!containsService(serviceMeta)) {
                throw new IllegalStateException("not find service, serviceMeta: " + serviceMeta);
            }
            services.remove(key(serviceMeta));

            Iterator<Entry> it = entrySet.iterator();
            while (it.hasNext()) {
                Entry entry = it.next();
                if (entry.getKey().equals(serviceMeta)) {
                    it.remove();
                }
            }
        }
    }

    public Channel get(ServiceMeta serviceMeta) {
        return services.get(key(serviceMeta));
    }

    public boolean containsService(ServiceMeta serviceMeta) {
        return services.containsKey(key(serviceMeta));
    }

    public void clear() {
        services.clear();
        entrySet.clear();
    }

    public int size() {
        return services.size();
    }

    public Set<Entry> entrySet() {
        return entrySet;
    }

    private String key(ServiceMeta serviceMeta) {
        String name = serviceMeta.getName();
        String address = serviceMeta.getAddress();
        return name + "-" + address;
    }

    @Override
    public String toString() {
        return "ServiceMap{" +
                "services=" + services +
                '}';
    }

    public static class Entry {
        private ServiceMeta key;
        private Channel value;

        public Entry(ServiceMeta key, Channel value) {
            this.key = key;
            this.value = value;
        }

        public ServiceMeta getKey() {
            return key;
        }

        public Channel getValue() {
            return value;
        }
    }
}
