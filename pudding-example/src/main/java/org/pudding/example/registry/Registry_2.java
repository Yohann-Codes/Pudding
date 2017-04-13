package org.pudding.example.registry;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.PuddingServiceRegistry;

/**
 * @author Yohann.
 */
public class Registry_1 {
    public static void main(String[] args) {
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20000);
    }
}
