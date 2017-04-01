package org.pudding.rpc.consumer.proxy;

import org.pudding.rpc.utils.ServiceMap;

/**
 * 全局使用，保存远程调用建立的连接.
 *
 * @author Yohann.
 */
public class ChannelManager {
    public static ServiceMap serviceMap;

    static {
        serviceMap = new ServiceMap();
    }
}
