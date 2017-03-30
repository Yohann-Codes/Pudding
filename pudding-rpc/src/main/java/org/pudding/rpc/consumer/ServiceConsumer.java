package org.pudding.rpc.consumer;

import org.pudding.rpc.consumer.future.ConsumerFuture;
import org.pudding.rpc.consumer.future.SubscribeFutureListener;

/**
 * 服务消费者.
 * <p>
 * 1) 连接注册中心.
 * 1) 订阅服务.
 *
 * @author Yohann.
 */
public interface ServiceConsumer {

    /**
     * 连接注册中心(目前注册中心只支持单机模式)
     * 注意: 调用此方法请必须在ConsumerConfig中配置过registryAddress, 否则抛异常NullPointerException.
     */
    ServiceConsumer connectRegistry();

    /**
     * 连接注册中心(目前注册中心只支持单机模式).
     *
     * @param registryAddress 格式: "host:port"  例如: "127.0.0.1:20000"
     */
    ServiceConsumer connectRegistry(String registryAddress);

    /**
     * 关闭与注册中心的连接.
     */
    void closeRegistry();

    /**
     * 订阅一个服务.
     *
     * @param serviceClazz
     */
    ServiceConsumer subscribeService(Class serviceClazz);

    /**
     * 订阅多个服务.
     *
     * @param serviceClazzs
     */
    ServiceConsumer subscribeServices(Class... serviceClazzs);

    /**
     * 添加订阅结果监听器.
     *
     * @param listener
     */
    void addSubscribeFutureListener(SubscribeFutureListener listener);
}
