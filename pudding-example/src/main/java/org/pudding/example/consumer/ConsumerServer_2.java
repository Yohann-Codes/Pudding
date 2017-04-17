package org.pudding.example.consumer;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.ServiceA;
import org.pudding.example.service.ServiceB;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.ServiceConsumer;

/**
 * @author Yohann.
 */
public class ConsumerServer_1 {
    public static void main(String[] args) {
        ServiceConsumer consumer = new DefaultServiceConsumer();
        consumer.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        ServiceMeta serviceMetaA = DefaultMetaFactory.createSubscribeMeta(ServiceA.class.getName());
        ServiceMeta serviceMetaB = DefaultMetaFactory.createSubscribeMeta(ServiceB.class.getName());

        consumer.subscribeServices(serviceMetaA, serviceMetaB);
    }
}
