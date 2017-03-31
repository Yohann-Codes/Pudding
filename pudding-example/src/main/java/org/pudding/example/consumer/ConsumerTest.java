package org.pudding.example.consumer;

import org.pudding.example.service.MyServiceA;
import org.pudding.example.service.MyServiceB;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.ServiceConsumer;

/**
 * @author Yohann.
 */
public class ConsumerTest {
    public static void main(String[] args) throws InterruptedException {
        // 创建服务消费者
        ServiceConsumer serviceConsumer = new DefaultServiceConsumer();
        // 连接注册中心
        serviceConsumer.connectRegistry("127.0.0.1:20000");
        // 订阅服务
        serviceConsumer.subscribeServices(MyServiceA.class, MyServiceB.class);
    }
}
