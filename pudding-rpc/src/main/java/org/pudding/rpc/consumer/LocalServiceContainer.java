package org.pudding.rpc.consumer;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Lists;
import org.pudding.common.utils.Maps;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Hold service that has registered.
 *
 * @author Yohann.
 */
public class LocalServiceContainer {

    private static final ConcurrentMap<String, List<ServiceMeta>> SERVICES = Maps.newConcurrentHashMap();

    public void put(ServiceMeta meta) {
        String name = meta.getName();
        List<ServiceMeta> metas;

        synchronized (SERVICES) {
            if (SERVICES.containsKey(name)) {
                metas = SERVICES.get(name);
                metas.add(meta);
            } else {
                metas = Lists.newArrayList();
                metas.add(meta);
                SERVICES.put(name, metas);
            }
        }
    }

    public void remove(ServiceMeta meta) {
        String name = meta.getName();

        synchronized (SERVICES) {
            if (SERVICES.containsKey(name)) {
                List<ServiceMeta> metaList = SERVICES.get(name);
                for (ServiceMeta m : metaList) {
                    if (m.equals(meta)) {
                        metaList.remove(m);
                        break;
                    }
                }
                if (metaList.size() == 0) {
                    SERVICES.remove(name);
                }
            }
        }
    }

    public List<ServiceMeta> get(String serviceName) {
        synchronized (SERVICES) {
            if (SERVICES.containsKey(serviceName)) {
                return SERVICES.get(serviceName);
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "LocalServiceContainer: " + SERVICES;
    }
}
