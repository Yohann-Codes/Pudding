package org.pudding.example.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.ServiceA;
import org.pudding.example.service.ServiceAImpl;
import org.pudding.example.service.ServiceB;
import org.pudding.example.service.ServiceBImpl;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.ServiceProvider;

/**
 * @author Yohann.
 */
public class ProviderServer_1 {
    public static void main(String[] args) {
        ServiceProvider provider = new DefaultServiceProvider();
        provider.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        ServiceA serviceA = new ServiceAImpl();
        ServiceB serviceB = new ServiceBImpl();

        ServiceMeta serviceMetaA = DefaultMetaFactory.createPublishMeta(serviceA, "127.0.0.1:30001", 50);
        ServiceMeta serviceMetaB = DefaultMetaFactory.createPublishMeta(serviceB, "127.0.0.1:30002", 50);

        provider.startServices(serviceMetaA, serviceMetaB);

        provider.publishAllService();
    }
}
