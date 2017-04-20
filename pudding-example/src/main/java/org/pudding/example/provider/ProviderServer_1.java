package org.pudding.example.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.ServiceA;
import org.pudding.example.service.ServiceAImpl;
import org.pudding.example.service.ServiceB;
import org.pudding.example.service.ServiceBImpl;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.ServiceProvider;

/**
 * @author Yohann.
 */
public class ProviderServer_1 {
    public static void main(String[] args) {
        // 创建提供者并连接注册中心
        ServiceProvider provider = new DefaultServiceProvider();
        provider.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        // 创建需要发布服务的元数据
        ServiceA serviceA = new ServiceAImpl();
        ServiceB serviceB = new ServiceBImpl();

        // 发布服务(权重范围0~10)
        ServiceMeta serviceMetaA = DefaultMetaFactory.createPublishMeta(serviceA, "127.0.0.1:30001", 5);
        ServiceMeta serviceMetaB = DefaultMetaFactory.createPublishMeta(serviceB, "127.0.0.1:30002", 5);

        // 启动服务
        provider.startServices(serviceMetaA, serviceMetaB);

        // 发布已启动的全部服务
        provider.publishAllService();
    }
}
