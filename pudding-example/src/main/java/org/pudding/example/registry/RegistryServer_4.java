package org.pudding.example.registry;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.PuddingServiceRegistry;

/**
 * @author Yohann.
 */
public class Registry_4 {
    public static void main(String[] args) {
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20004);
        registry.joinUpCluster("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003");
    }
}
