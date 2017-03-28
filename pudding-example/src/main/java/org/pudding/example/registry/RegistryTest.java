package org.pudding.example.registry;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.ServiceRegistry;

/**
 * @author Yohann.
 */
public class RegistryTest {
    public static void main(String[] args) {
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry();
    }
}
