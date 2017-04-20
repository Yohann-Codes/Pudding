# Pudding&#12288;一款迷你级分布式服务框架

- 注册中心支持集群模式
- 服务自动发现
- 同步／异步调用
- 负载均衡
- 限流

### 使用示例
#### 1. 启动注册中心 (集群)
```
public class RegistryServer_1 {
    public static void main(String[] args) throws InterruptedException {
        // 启动注册中心
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20001);

        // 等待其它注册服务器启动完成(时间尽量长些)
        Thread.sleep(15000);

        // 接入注册中心集群
        registry.joinUpCluster("127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");
    }
}
```

```
public class RegistryServer_2 {
    public static void main(String[] args) {
        // 启动注册中心
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20002);

        // 接入注册中心集群
        registry.joinUpCluster( "127.0.0.1:20003", "127.0.0.1:20004", "127.0.0.1:20001");
    }
}
```

```
public class RegistryServer_3 {
    public static void main(String[] args) {
        // 启动注册中心
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20003);

        // 接入注册中心集群
        registry.joinUpCluster( "127.0.0.1:20004", "127.0.0.1:20001", "127.0.0.1:20002");
    }
}
```

```
public class RegistryServer_4 {
    public static void main(String[] args) {
        // 启动注册中心
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20004);

        // 接入注册中心集群
        registry.joinUpCluster("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003");
    }
}
```

#### 2. 启动服务提供者
```
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
```

```
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
```

#### 3. 启动服务消费者
```
public class ConsumerServer_1 {
    public static void main(String[] args) {
        // 创建消费者并连接注册中心
        ServiceConsumer consumer = new DefaultServiceConsumer();
        consumer.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        // 创建需要订阅服务的元数据
        ServiceMeta serviceMetaA = DefaultMetaFactory.createSubscribeMeta(ServiceA.class.getName());
        ServiceMeta serviceMetaB = DefaultMetaFactory.createSubscribeMeta(ServiceB.class.getName());

        // 订阅服务(同步方式，订阅成功后返回)
        consumer.subscribeServices(serviceMetaA, serviceMetaB);

        // 创建同步调用代理服务
        ServiceA serviceA = ServiceProxyFactory.createSyncProxy(ServiceA.class);
        ServiceB serviceB = ServiceProxyFactory.createSyncProxy(ServiceB.class);

        // 同步调用(监听器参数传null即可)
        try {
            int add = serviceA.add(100, 200, null);
            System.out.println("100 + 200 = " + add);
            int subtract = serviceB.subtract(200, 100, null);
            System.out.println("200 - 100 = " + subtract);
        } catch (InvokeTimeoutException e) {
            System.out.println("调用超时");
        } catch (NotFindServiceException e) {
            System.out.println("未找到服务");
        } catch (ServiceBusyException e) {
            System.out.println("服务繁忙");
        }
    }
}
```

```
public class ConsumerServer_2 {
    public static void main(String[] args) {
        // 创建消费者并连接注册中心
        ServiceConsumer consumer = new DefaultServiceConsumer();
        consumer.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        // 创建需要订阅服务的元数据
        ServiceMeta serviceMetaC = DefaultMetaFactory.createSubscribeMeta(ServiceC.class.getName());
        ServiceMeta serviceMetaD = DefaultMetaFactory.createSubscribeMeta(ServiceD.class.getName());

        // 订阅服务(同步方式，订阅成功后返回)
        consumer.subscribeServices(serviceMetaC, serviceMetaD);

        // 创建异步调用代理服务
        ServiceC serviceC = ServiceProxyFactory.createAsyncProxy(ServiceC.class);
        ServiceD serviceD = ServiceProxyFactory.createAsyncProxy(ServiceD.class);

        // 异步调用
        serviceC.multiply(100, 200, new InvokerFutureListener<Integer>() {
            @Override
            public void success(Integer result) {
                System.out.println("100 * 200 = " + result);
            }

            @Override
            public void failure(Exception e) {
                e.printStackTrace();
            }
        });

        serviceD.divide(200, 100, new InvokerFutureListener<Integer>() {
            @Override
            public void success(Integer result) {
                System.out.println("200 / 100 = " + result);
            }

            @Override
            public void failure(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
```