package org.pudding.rpc.consumer.load_balance;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.RandomUtil;
import org.pudding.rpc.consumer.LocalServiceContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Weighted random load balancer.
 *
 * @author Yohann.
 */
public class WeightRandomBalancer implements LoadBalancer {
    private final LocalServiceContainer localServiceContainer = new LocalServiceContainer();

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

        // [0, weightServiceMetas.size)
        int index = RandomUtil.getInt(weightServiceMetas.size());

        return weightServiceMetas.get(index);
    }
}
