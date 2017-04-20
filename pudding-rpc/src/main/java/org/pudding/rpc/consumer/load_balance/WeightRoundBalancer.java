package org.pudding.rpc.consumer.load_balance;

import org.pudding.common.model.ServiceMeta;
import org.pudding.rpc.consumer.LocalServiceContainer;

import java.util.Iterator;
import java.util.List;

/**
 * Weighted round load balancer.
 *
 * @author Yohann.
 */
public class WgtRoundLoadBalancer implements LoadBalancer {
    private final LocalServiceContainer localServiceContainer = new LocalServiceContainer();

    private int serviceIndex = 0;

    @Override
    public ServiceMeta select(String serviceName) {
        List<ServiceMeta> serviceMetas = localServiceContainer.get(serviceName);
        Iterator<ServiceMeta> it = serviceMetas.iterator();

        List<ServiceMeta>
        while (it.hasNext()) {
            ServiceMeta meta = it.next();
        }

        synchronized (this) {
            if (serviceIndex > size - 1) {
                // reset
                serviceIndex = 0;
            } else {
                serviceIndex++;
            }
        }

        return serviceMetas.get(serviceIndex);
    }
}
