package org.pudding.rpc.consumer.local_service;

import org.pudding.common.model.ServiceMeta;

import java.util.List;

/**
 * 管理订阅到本地的服务信息.
 *
 * @author Yohann.
 */
public interface LocalManager {

    /**
     * 将订阅结果缓存到本地.
     *
     * @param serviceMeta
     */
    void cacheService(ServiceMeta serviceMeta);

    /**
     * 根据服务名查询服务.
     *
     * @param serviceName
     * @return
     */
    List<ServiceMeta> queryService(String serviceName);
}
