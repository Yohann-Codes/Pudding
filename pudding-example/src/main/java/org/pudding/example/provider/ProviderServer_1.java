package org.pudding.example.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.MyServiceA;
import org.pudding.example.service.MyServiceAImpl;
import org.pudding.example.service.MyServiceB;
import org.pudding.example.service.MyServiceBImpl;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.ServiceMetaFactory;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.ServiceProvider;

/**
 * @author Yohann.
 */
public class ProviderServer {
    public static void main(String[] args) {
        ServiceProvider provider = new DefaultServiceProvider();
        provider.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        MyServiceA serviceA = new MyServiceAImpl();
        MyServiceB serviceB = new MyServiceBImpl();

        ServiceMeta serviceMetaA = DefaultMetaFactory.createPublishMeta(serviceA, "127.0.0.1:30001", 50);
        ServiceMeta serviceMetaB = DefaultMetaFactory.createPublishMeta(serviceB, "127.0.0.1:30002", 50);

        provider.startServices(serviceMetaA, serviceMetaB);
    }
}
