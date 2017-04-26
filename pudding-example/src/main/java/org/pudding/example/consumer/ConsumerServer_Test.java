package org.pudding.example.consumer;

import org.pudding.common.exception.InvokeTimeoutException;
import org.pudding.common.exception.NotFindServiceException;
import org.pudding.common.exception.ServiceBusyException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.ServiceA;
import org.pudding.example.service.ServiceB;
import org.pudding.example.service.ServiceC;
import org.pudding.example.service.ServiceD;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.ServiceConsumer;
import org.pudding.rpc.consumer.invoker.ServiceProxyFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Yohann.
 */
public class ConsumerServer_Test {
    public static void main(String[] args) {
        ServiceConsumer consumer = new DefaultServiceConsumer();
        consumer.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        ServiceMeta serviceMetaA = DefaultMetaFactory.createSubscribeMeta(ServiceA.class.getName());
        ServiceMeta serviceMetaB = DefaultMetaFactory.createSubscribeMeta(ServiceB.class.getName());
        ServiceMeta serviceMetaC = DefaultMetaFactory.createSubscribeMeta(ServiceC.class.getName());
        ServiceMeta serviceMetaD = DefaultMetaFactory.createSubscribeMeta(ServiceD.class.getName());

        consumer.subscribeServices(serviceMetaA, serviceMetaB, serviceMetaC, serviceMetaD);

        final ServiceA serviceA = ServiceProxyFactory.createSyncProxy(ServiceA.class);
        final ServiceB serviceB = ServiceProxyFactory.createSyncProxy(ServiceB.class);
        final ServiceC serviceC = ServiceProxyFactory.createSyncProxy(ServiceC.class);
        final ServiceD serviceD = ServiceProxyFactory.createSyncProxy(ServiceD.class);

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (;;) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        int add = serviceA.add(100, 200, null);
                        System.out.println("100 + 200 = " + add);
                    } catch (InvokeTimeoutException e) {
                        System.out.println("调用超时");
                    } catch (NotFindServiceException e) {
                        System.out.println("未找到服务");
                    } catch (ServiceBusyException e) {
                        System.out.println("服务繁忙");
                    }
                }
            });

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
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
            });

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        int multiply = serviceC.multiply(100, 200, null);
                        System.out.println("200 * 100 = " + multiply);
                    } catch (InvokeTimeoutException e) {
                        System.out.println("调用超时");
                    } catch (NotFindServiceException e) {
                        System.out.println("未找到服务");
                    } catch (ServiceBusyException e) {
                        System.out.println("服务繁忙");
                    }
                }
            });

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        int divide = serviceD.divide(200, 100, null);
                        System.out.println("200 / 100 = " + divide);
                    } catch (InvokeTimeoutException e) {
                        System.out.println("调用超时");
                    } catch (NotFindServiceException e) {
                        System.out.println("未找到服务");
                    } catch (ServiceBusyException e) {
                        System.out.println("服务繁忙");
                    }
                }
            });

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
