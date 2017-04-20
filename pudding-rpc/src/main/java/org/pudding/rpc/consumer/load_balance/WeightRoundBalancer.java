package org.pudding.rpc.consumer.load_balance;

import org.pudding.common.model.ServiceMeta;
import org.pudding.rpc.consumer.LocalServiceContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Weighted round load balancer.
 *
 * @author Yohann.
 */
public class WeightRoundBalancer implements LoadBalancer {
    private final LocalServiceContainer localServiceContainer = new LocalServiceContainer();

    private int serviceIndex = 0;

    @Override
    public ServiceMeta select(String serviceName) {
        List<ServiceMeta> serviceMetas = localServiceContainer.get(serviceName);

        if (serviceMetas == null) {
            return null;
        }

        if (serviceMetas.size() == 1) {
            return serviceMetas.get(0);
        }

        List<ServiceMeta> weightServiceMetas = new ArrayList<>();
        Iterator<ServiceMeta> it = serviceMetas.iterator();
        while (it.hasNext()) {
            ServiceMeta meta = it.next();
            for (int i = 0; i < meta.getWeight(); i++) {
                weightServiceMetas.add(meta);
            }
        }

        synchronized (this) {
            if (serviceIndex > weightServiceMetas.size() - 1) {
                // reset
                serviceIndex = 0;
            } else {
                serviceIndex++;
            }
        }

        return weightServiceMetas.get(serviceIndex);
    }
}
