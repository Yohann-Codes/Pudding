package org.pudding.example.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.MyService1;
import org.pudding.example.service.MyService2;
import org.pudding.example.service.MyServiceImpl1;
import org.pudding.example.service.MyServiceImpl2;
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
        MyService1 service1 = new MyServiceImpl1();
        MyService2 service2 = new MyServiceImpl2();

        // 包装服务为ServiceMeta
        ServiceMeta serviceMeta1 = serviceWrapper.build(service1, "127.0.0.1:30001");
        ServiceMeta serviceMeta2 = serviceWrapper.build(service2, "127.0.0.1:30002");

        // 启用服务
        provider.startServices(serviceMeta1, serviceMeta2);
        // 发布所有已启用的服务
        provider.publishAllService();

        Thread.sleep(2000);
        provider.closeRegistry();
    }
}
