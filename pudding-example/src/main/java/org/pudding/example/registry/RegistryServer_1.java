package org.pudding.example.registry;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.PuddingServiceRegistry;

/**
 * @author Yohann.
 */
public class Registry_1 {
    public static void main(String[] args) throws InterruptedException {
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20001);

        Thread.sleep(10000);
        registry.joinUpCluster("127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");
    }
}
