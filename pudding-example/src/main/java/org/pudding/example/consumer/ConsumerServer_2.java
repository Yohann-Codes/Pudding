package org.pudding.example.consumer;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.ServiceA;
import org.pudding.example.service.ServiceB;
import org.pudding.example.service.ServiceC;
import org.pudding.example.service.ServiceD;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.ServiceConsumer;
import org.pudding.rpc.consumer.invoker.ServiceProxyFactory;
import org.pudding.rpc.consumer.invoker.future.InvokerFutureListener;

/**
 * @author Yohann.
 */
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
