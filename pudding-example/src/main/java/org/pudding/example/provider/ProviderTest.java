package org.pudding.example.provider;

import org.pudding.common.exception.ServicePublishFailedException;
import org.pudding.common.exception.ServiceStartFailedException;
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
        // 构造服务元数据
        ServiceMeta serviceMeta = wrapper.build(service, "127.0.0.1:30001");
        try {
            // 发布并启用服务
            provider.publishService(serviceMeta);
            provider.startService();
        } catch (ServicePublishFailedException e) {
            e.printStackTrace();
        } catch (ServiceStartFailedException e) {
            e.printStackTrace();
            provider.unpublishService(serviceMeta); // 取消发布
        }
    }
}
