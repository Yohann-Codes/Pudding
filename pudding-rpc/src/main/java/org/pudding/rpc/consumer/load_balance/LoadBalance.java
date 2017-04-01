package org.pudding.rpc.consumer.load_balance;

import org.pudding.common.constant.LoadBalanceStrategy;
import org.pudding.common.model.InvokeMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.rpc.consumer.config.ConsumerConfig;
import org.pudding.rpc.consumer.local_service.DefaultLocalManager;
import org.pudding.rpc.consumer.local_service.LocalManager;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡控制.
 *
 * @author Yohann.
 */
public class LoadBalance {

    private final LocalManager localManager;

    public LoadBalance() {
        localManager = DefaultLocalManager.getLocalManager();
    }

    /**
     * 在本地服务列表中选择服务地址.
     *
     * @param invokeMeta
     * @return
     */
    public ServiceMeta selectService(InvokeMeta invokeMeta) {
        checkNotNull(invokeMeta);

        ServiceMeta serviceMeta = null;
        String serviceName = invokeMeta.getServiceName();

        switch (ConsumerConfig.loadBalanceStrategy()) {
            case LoadBalanceStrategy.RANDOM:
                serviceMeta = random(serviceName);
                break;
        }

        return serviceMeta;
    }

    /**
     * Random.
     *
     * @param serviceName
     */
    private ServiceMeta random(String serviceName) {
        ServiceMeta serviceMeta; // 被选中的服务
        List<ServiceMeta> serviceMetas = localManager.queryService(serviceName);
        int size = serviceMetas.size();
        if (size < 2) {
            serviceMeta = serviceMetas.get(0);
        } else {
            // 生成在[0, size)范围内的随机整数, 作为serviceMetas索引
            Random random = new Random();
            int index = random.nextInt(size);
            serviceMeta = serviceMetas.get(index);
        }
        return serviceMeta;
    }

    private void checkNotNull(InvokeMeta invokeMeta) {
        if (invokeMeta == null) {
            throw new NullPointerException("invokeMeta == null");
        }
    }
}
