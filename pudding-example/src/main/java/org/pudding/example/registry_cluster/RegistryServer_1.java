package org.pudding.example.registry;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.PuddingServiceRegistry;

/**
 * @author Yohann.
 */
public class RegistryServer_1 {
    public static void main(String[] args) throws InterruptedException {
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20001);

        Thread.sleep(15000); // Wait util other server started
        registry.joinUpCluster("127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");
    }
}
