package org.pudding.example.consumer;

import org.pudding.example.service.MyService;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.ServiceConsumer;
import org.pudding.rpc.consumer.future.InvokeFuture;
import org.pudding.rpc.consumer.future.InvokeFutureListener;
import org.pudding.rpc.consumer.proxy.ProxyFactory;

/**
 * @author Yohann.
 */
public class AsyncConsumerTest {
    public static void main(String[] args) {
        // 创建服务消费者
        ServiceConsumer serviceConsumer = new DefaultServiceConsumer();
        // 连接注册中心
        serviceConsumer.connectRegistry("127.0.0.1:20000");
        // 订阅服务
        serviceConsumer.subscribeService(MyService.class);
        // 创建同步服务代理
        MyService myService = ProxyFactory.createAsyncProxy(MyService.class);
        // 发起调用
        myService.add(100, 200);
        // 监听调用结果
        InvokeFuture.addInvokeFutureListener(new InvokeFutureListener<Integer>() {
            @Override
            public void success(Integer result) {
                System.out.println("调用结果: " + result);
            }

            @Override
            public void failure(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
