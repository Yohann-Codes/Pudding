package org.pudding.example.registry_cluster;

import org.pudding.registry.DefaultServiceRegistry;
import org.pudding.registry.PuddingServiceRegistry;

/**
 * @author Yohann.
 */
public class RegistryServer_1 {
    public static void main(String[] args) throws InterruptedException {
        // 启动注册中心
        PuddingServiceRegistry registry = new DefaultServiceRegistry();
        registry.startRegistry(20001);

        // 等待其它注册服务器启动完成(时间尽量长些)
        Thread.sleep(15000);

        // 接入注册中心集群
        registry.joinUpCluster("127.0.0.1:20002", "127.0.0.1:20003", "127.0.0.1:20004");
    }
}
