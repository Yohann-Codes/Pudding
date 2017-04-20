package org.pudding.example.registry_cluster;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.PuddingServiceRegistry;

/**
 * @author Yohann.
 */
public class RegistryServer_4 {
    public static void main(String[] args) {
        // 启动注册中心
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20004);

        // 接入注册中心集群
        registry.joinUpCluster("127.0.0.1:20001", "127.0.0.1:20002", "127.0.0.1:20003");
    }
}
