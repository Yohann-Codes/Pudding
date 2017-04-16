package org.pudding.registry;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Maps;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Hold service that has registered.
 *
 * @author Yohann.
 */
public class ServiceContainer {

    private ConcurrentMap<String, List<ServiceMeta>> services = Maps.newConcurrentHashMap();

    public void put(ServiceMeta meta) {
        String name = meta.getName();

        if (services.containsKey(name)) {
            List<ServiceMeta> metas = services.get(name);
            metas.add(meta);
        }
    }
}
