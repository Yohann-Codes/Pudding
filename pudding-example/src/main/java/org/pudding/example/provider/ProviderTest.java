package org.pudding.example.provider;

import org.pudding.common.model.ServiceMeta;
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
    public static void main(String[] args) {
        // 创建服务提供者和服务包装器
        ServiceProvider provider = new DefaultServiceProvider();
        ServiceWrapper wrapper = new DefaultServiceWrapper();
        // 创建服务
        MyService service = new MyServiceImpl();
        // 连接注册中心
        provider.connectRegistry("127.0.0.1:20000");
        // 构造服务元数据并发布服务
        ServiceMeta serviceMeta = wrapper.build(service, "127.0.0.1:30001");
        provider.publishService(serviceMeta);
        // 启动服务
        provider.startService();
    }
}
