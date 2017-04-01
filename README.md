# Pudding
一款轻量级分布式服务框架

## 使用示例
### 第一步: 启动注册中心
```
public class RegistryTest {
    public static void main(String[] args) {
        // 创建服务注册中心
        ServiceRegistry registry = new DefaultServiceRegistry();
        // 启动注册中心
        registry.startRegistry(20000);
    }
}
```

### 第二步: 启动服务提供者
```
public class ProviderTest {
    public static void main(String[] args) {
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
```

### 第三步: 启动服务消费者
```
public class ConsumerTest {
    public static void main(String[] args) {
        // 创建服务消费者
        ServiceConsumer serviceConsumer = new DefaultServiceConsumer();
        // 连接注册中心
        serviceConsumer.connectRegistry("127.0.0.1:20000");
        // 订阅服务
        serviceConsumer.subscribeService(MyService.class);
        // 创建同步服务代理
        MyService myService = ProxyFactory.createSyncProxy(MyService.class);
        try {
            // 发起调用
            int result = myService.add(100, 200);
            System.out.println("调用结果: " + result);
        } catch (InvokeTimeoutException e) {
            System.out.println("远程调用超时");
        } catch (InvokeFailedException e) {
            System.out.println("远程调用失败");
        }
    }
}
```