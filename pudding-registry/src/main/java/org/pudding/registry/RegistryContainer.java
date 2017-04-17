package org.pudding.registry;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Lists;
import org.pudding.common.utils.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Hold service that has registered.
 *
 * @author Yohann.
 */
public class RegistryContainer {

    private ConcurrentMap<String, List<ServiceMeta>> services = Maps.newConcurrentHashMap();

    public void put(ServiceMeta meta) {
        String name = meta.getName();
        List<ServiceMeta> metas;

        synchronized (services) {
            if (services.containsKey(name)) {
                metas = services.get(name);
                metas.add(meta);
            } else {
                metas = Lists.newArrayList();
                metas.add(meta);
                services.put(name, metas);
            }
        }
    }

    public void remove(ServiceMeta meta) {
        String name = meta.getName();

        synchronized (services) {
            if (services.containsKey(name)) {
                List<ServiceMeta> metaList = services.get(name);
                for (ServiceMeta m : metaList) {
                    if (m.equals(meta)) {
                        metaList.remove(m);
                        break;
                    }
                }
                if (metaList.size() == 0) {
                    services.remove(name);
                }
            }
        }
    }

    public List<ServiceMeta> get(String serviceName) {
        synchronized (services) {
            if (services.containsKey(serviceName)) {
                return services.get(serviceName);
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "RegistryContainer: " +  services;
    }
}
