package org.pudding.example.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.*;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.ServiceProvider;

/**
 * @author Yohann.
 */
public class ProviderServer_2 {
    public static void main(String[] args) {
        // 创建提供者并连接注册中心
        ServiceProvider provider = new DefaultServiceProvider();
        provider.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        // 创建需要发布服务的元数据
        ServiceC serviceC = new ServiceCImpl();
        ServiceD serviceD = new ServiceDImpl();

        // 发布服务(权重范围0~10)
        ServiceMeta serviceMetaC = DefaultMetaFactory.createPublishMeta(serviceC, "127.0.0.1:30003", 5);
        ServiceMeta serviceMetaD = DefaultMetaFactory.createPublishMeta(serviceD, "127.0.0.1:30004", 5);

        // 启动服务
        provider.startServices(serviceMetaC, serviceMetaD);

        // 发布已启动的全部服务
        provider.publishAllService();
    }
}
