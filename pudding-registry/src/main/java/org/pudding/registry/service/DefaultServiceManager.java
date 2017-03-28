package org.pudding.registry.service;

import org.apache.log4j.Logger;
import org.pudding.common.exception.ServiceNotPublishedException;
import org.pudding.common.exception.ServicePublishFailedException;
import org.pudding.common.model.ServiceMeta;

import java.util.*;

/**
 * 默认的ServiceManager实现.
 *
 * @author Yohann.
 */
public class DefaultServiceManager implements ServiceManager {
    private static final Logger logger = Logger.getLogger(DefaultServiceManager.class);

    private Object lock = new Object();

    // 保存已发布的服务, 存在并发性
    // Map: key -> 服务名 , List -> 不同提供者的同名服务
    private volatile Map<String, List<ServiceMeta>> services;

    public DefaultServiceManager() {
        services = new HashMap<>();
    }

    @Override
    public ServiceMeta registerService(ServiceMeta serviceMeta) throws ServicePublishFailedException {
        validate(serviceMeta);
        String name = serviceMeta.getName();
        synchronized (lock) {
            if (services.containsKey(name)) {
                // 已经有提供者注册了此服务
                List<ServiceMeta> serviceList = services.get(name);
                // 检查是否二次注册
                for (ServiceMeta meta : serviceList) {
                    if (meta.getAddress().equals(serviceMeta.getAddress())) {
                        throw new ServicePublishFailedException("The service has been published: " + serviceMeta);
                    }
                }
                serviceList.add(serviceMeta);
            } else {
                // 没有提供者注册此服务
                List<ServiceMeta> serviceList = new ArrayList<>();
                serviceList.add(serviceMeta);
                services.put(name, serviceList);
            }
        }
        return serviceMeta;
    }

    @Override
    public ServiceMeta unregisterService(ServiceMeta serviceMeta) {
        validate(serviceMeta);
        String name = serviceMeta.getName();
        synchronized (lock) {
            if (services.containsKey(name)) {
                List<ServiceMeta> serviceList = services.get(name);
                if (serviceList.size() == 1) {
                    services.remove(name);
                } else {
                    Iterator<ServiceMeta> ite = serviceList.iterator();
                    while (ite.hasNext()) {
                        ServiceMeta meta = ite.next();
                        if (meta.getAddress().equals(serviceMeta.getAddress())) {
                            ite.remove();
                        }
                    }
                }
            } else {
                throw new ServiceNotPublishedException("There is no this service: " + serviceMeta);
            }
        }
        return serviceMeta;
    }

    private void validate(ServiceMeta serviceMeta) {
        if (serviceMeta == null) {
            throw new NullPointerException("service == null");
        }
    }
}