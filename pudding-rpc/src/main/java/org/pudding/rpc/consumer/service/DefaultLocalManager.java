package org.pudding.rpc.consumer.service;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认服务管理实现(单例模式).
 *
 * @author Yohann.
 */
public class DefaultLocalManager implements LocalManager {
    private static final Logger logger = Logger.getLogger(DefaultLocalManager.class);

    private static LocalManager localManager;

    private Object lock = new Object();

    // 保存已发布的服务, 存在并发性
    // Map: key -> 服务名 , List -> 不同提供者的同名服务
    private volatile Map<String, List<ServiceMeta>> services;

    private DefaultLocalManager() {
        services = new HashMap<>();
    }

    public static LocalManager getLocalManager() {
        if (localManager == null) {
            synchronized (DefaultLocalManager.class) {
                if (localManager == null) {
                    localManager = new DefaultLocalManager();
                }
            }
        }
        return localManager;
    }

    @Override
    public void cacheService(ServiceMeta serviceMeta) {
        validate(serviceMeta);
        String name = serviceMeta.getName();
        List<ServiceMeta> serviceMetas;
        synchronized (lock) {
            if (services.containsKey(name)) {
                serviceMetas = services.get(name);
            } else {
                serviceMetas = new ArrayList<>();
                services.put(name, serviceMetas);
            }
            serviceMetas.add(serviceMeta);
        }
    }

    @Override
    public List<ServiceMeta> queryService(String serviceName) {
        return services.get(serviceName);
    }

    private void validate(ServiceMeta serviceMeta) {
        if (serviceMeta == null) {
            throw new NullPointerException("service == null");
        }
    }
}
