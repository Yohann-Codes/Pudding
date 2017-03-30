package org.pudding.rpc.provider;

import org.pudding.common.model.ServiceMeta;

/**
 * 服务提供者.
 * <p>
 * 1) 连接注册中心.
 * 2) 启动服务.
 * 3) 发布服务.
 * 4) 取消服务.
 * 5) 停止服务。
 *
 * @author Yohann.
 */
public interface ServiceProvider {

    /**
     * 连接注册中心(目前注册中心只支持单机模式)
     * 注意: 调用此方法请必须在ProviderConfig中配置过registryAddress, 否则抛异常NullPointerException.
     */
    ServiceProvider connectRegistry();

    /**
     * 连接注册中心(目前注册中心只支持单机模式).
     *
     * @param registryAddress 格式: "host:port"  例如: "127.0.0.1:20000"
     */
    ServiceProvider connectRegistry(String registryAddress);

    /**
     * 关闭与注册中心的连接.
     * 如果发布完服务后较长一段时间不需要再发布，就可以调用此方法断开连接，
     * 下次发布前调用connectRegistry()建立连接即可.
     */
    void closeRegistry();

    /**
     * 启用一个服务.
     */
    ServiceProvider startService(ServiceMeta serviceMeta);

    /**
     * 启用多个服务.
     */
    ServiceProvider startServices(ServiceMeta... serviceMetas);

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
     * 发布在此实例上启用的所有服务.
     */
    ServiceProvider publishAllService();

    /**
     * 启用并发布一个服务.
     *
     * @param serviceMeta
     */
    ServiceProvider startAndPublishService(ServiceMeta serviceMeta);

    /**
     * 启用并发布多个服务.
     *
     * @param serviceMetas
     */
    ServiceProvider startAndPublishServices(ServiceMeta... serviceMetas);

    /**
     * 取消已发布的服务.
     *
     * @param serviceMeta
     */
    ServiceProvider unpublishService(ServiceMeta serviceMeta);

    /**
     * 停止服务.
     *
     * @param serviceMeta
     */
    ServiceProvider stopService(ServiceMeta serviceMeta);

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