package org.pudding.example.consumer;

import org.apache.log4j.Logger;
import org.pudding.example.service.MyService1;
import org.pudding.example.service.MyService2;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.ServiceConsumer;
import org.pudding.rpc.consumer.future.SubscribeFutureListener;

import java.util.List;

/**
 * @author Yohann.
 */
public class ConsumerTest {
    private static final Logger logger = Logger.getLogger(ConsumerTest.class);

    public static void main(String[] args) {
        ServiceConsumer serviceConsumer = new DefaultServiceConsumer();
        // 连接注册中心
        serviceConsumer.connectRegistry("127.0.0.1:20000");
        // 订阅服务
        serviceConsumer.subscribeServices(MyService1.class, MyService2.class);
        // 监听订阅结果
        serviceConsumer.addSubscribeFutureListener(new SubscribeFutureListener() {
            @Override
            public void suscribeComplete(boolean isSuccess, String serviceName, List<String> serviceAddress) {
                if (isSuccess) {
                    logger.info("订阅成功: " + serviceName + " " + serviceAddress);
                } else {
                    logger.info("订阅失败: " + serviceName);
                }
            }
        });
    }
}
