package org.pudding.example.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.MyServiceA;
import org.pudding.example.service.MyServiceB;
import org.pudding.example.service.MyServiceImplA;
import org.pudding.example.service.MyServiceImplB;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.DefaultServiceWrapper;
import org.pudding.rpc.provider.ServiceProvider;
import org.pudding.rpc.provider.ServiceWrapper;

/**
 * @author Yohann.
 */
public class ProviderTest {
    public static void main(String[] args) throws InterruptedException {
        // 创建服务提供者和服务包装器
        ServiceProvider provider = new DefaultServiceProvider();
        ServiceWrapper serviceWrapper = new DefaultServiceWrapper();

        // 连接注册中心
        provider.connectRegistry("127.0.0.1:20000");

        // 创建服务
        MyServiceA serviceA = new MyServiceImplA();
        MyServiceB serviceB = new MyServiceImplB();

        // 包装服务为ServiceMeta
        ServiceMeta serviceMetaA = serviceWrapper.build(serviceA, "127.0.0.1:30001");
        ServiceMeta serviceMetaB = serviceWrapper.build(serviceB, "127.0.0.1:30002");

        // 启用服务
        provider.startServices(serviceMetaA, serviceMetaB);
        // 发布所有已启用的服务
        provider.publishAllService();
    }
}
