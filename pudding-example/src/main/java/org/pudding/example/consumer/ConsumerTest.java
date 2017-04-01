package org.pudding.example.consumer;

import org.pudding.common.exception.InvokeTimeoutException;
import org.pudding.example.service.MyService;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.ServiceConsumer;
import org.pudding.rpc.consumer.proxy.ProxyFactory;

/**
 * @author Yohann.
 */
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
            System.out.println("远程调用超时了!");
        }
    }
}
