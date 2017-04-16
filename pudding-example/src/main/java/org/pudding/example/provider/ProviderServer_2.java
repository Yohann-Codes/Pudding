package org.pudding.example.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.example.service.*;
import org.pudding.rpc.DefaultMetaFactory;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.ServiceProvider;

/**
 * @author Yohann.
 */
public class ProviderServer_2 {
    public static void main(String[] args) {
        ServiceProvider provider = new DefaultServiceProvider();
        provider.connectRegistry("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");

        ServiceC serviceC = new ServiceCImpl();
        ServiceD serviceD = new ServiceDImpl();

        ServiceMeta serviceMetaC = DefaultMetaFactory.createPublishMeta(serviceC, "127.0.0.1:30003", 50);
        ServiceMeta serviceMetaD = DefaultMetaFactory.createPublishMeta(serviceD, "127.0.0.1:30004", 50);

        provider.startServices(serviceMetaC, serviceMetaD);

        provider.publishAllService();
    }
}
