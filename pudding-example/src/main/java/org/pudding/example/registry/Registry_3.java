package org.pudding.example.registry;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.PuddingServiceRegistry;

/**
 * @author Yohann.
 */
public class Registry_3 {
    public static void main(String[] args) {
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20003);
        registry.joinUpCluster("127.0.0.1:20001", "127.0.0.1:20002");
    }
}
