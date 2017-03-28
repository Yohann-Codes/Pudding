package org.pudding.example.registry;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.ServiceRegistry;

/**
 * @author Yohann.
 */
public class RegistryTest {
    public static void main(String[] args) {
        // 创建服务注册中心
        ServiceRegistry registry = new DefaultServiceRegistry();
        // 启动注册中心
        registry.startRegistry(20000);
    }
}
