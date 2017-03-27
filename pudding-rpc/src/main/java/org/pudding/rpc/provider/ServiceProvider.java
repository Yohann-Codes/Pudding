package org.pudding.rpc.provider;

import org.pudding.common.model.ServiceMeta;

/**
 * 服务提供者.
 * <p>
 * 最终要的两个功能:
 * 1) 连接注册中心发布服务.
 * 2) 启动本地服务.
 *
 * @author Yohann.
 */
public interface ServiceProvider {

    /**
     * 连接注册中心(目前注册中心只支持单机模式).
     *
     * @param registryAddress 格式: "host:port"  例如: "127.0.0.1:20000"
     */
    ServiceProvider connectRegistry(String registryAddress);

    /**
     * 发布一个服务.
     *
     * @param serviceMeta
     */
    ServiceProvider publishService(ServiceMeta serviceMeta);

    /**
     * 发布多个服务.
     *
     * @param serviceMetas
     */
    ServiceProvider publishServices(ServiceMeta... serviceMetas);

    /**
     * 启动本地服务(绑定端口监听远程调用).
     * 注意: 调用此方法之前必须先发布服务.
     */
    ServiceProvider startService();

    /**
     * 发布并启动一个服务.
     *
     * @param serviceMeta
     */
    ServiceProvider publishAndStartService(ServiceMeta serviceMeta);

    /**
     * 发布并启动多个服务.
     *
     * @param serviceMetas
     */
    ServiceProvider publishAndStartServices(ServiceMeta... serviceMetas);

    /**
     * 取消发布并停止服务.
     *
     * @param serviceMeta
     */
    ServiceProvider unpublishAndStopService(ServiceMeta serviceMeta);

    /**
     * 取消发布并停止在此实例上的全部服务.
     */
    ServiceProvider unpublishAndStopAll();
}
