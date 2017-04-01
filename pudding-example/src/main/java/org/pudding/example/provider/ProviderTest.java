package org.pudding.example.provider;

import org.pudding.common.model.Service;
import org.pudding.example.service.MyService;
import org.pudding.example.service.MyServiceImpl;
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
        MyService myService = new MyServiceImpl();
        // 包装服务为ServiceMeta
        Service service = serviceWrapper.build(myService, "127.0.0.1:30001");
        // 启用服务
        provider.startService(service);
        // 发布所有已启用的服务
        provider.publishAllService();
    }
}
