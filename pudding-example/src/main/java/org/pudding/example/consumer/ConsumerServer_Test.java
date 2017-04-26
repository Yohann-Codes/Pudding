package org.pudding.example.consumer;

import org.pudding.common.exception.InvokeTimeoutException;
import org.pudding.common.exception.NotFindServiceException;
import org.pudding.common.exception.ServiceBusyException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.ServiceA;
import org.pudding.example.service.ServiceB;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.ServiceConsumer;
import org.pudding.rpc.consumer.invoker.ServiceProxyFactory;

/**
 * @author Yohann.
 */
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
