package org.pudding.rpc.consumer.future;

import java.util.List;

/**
 * 服务订阅监听器.
 *
 * @author Yohann.
 */
public interface SubscribeFutureListener extends FutureListener {

    /**
     * 服务订阅得到响应后由ServiceConsumer回调此方法.
     * 这样用户可以知道哪些服务订阅成功哪些订阅失败.
     *
     * @param isSuccess 是否订阅成功
     * @param serviceName 服务名称
     * @param serviceAddress 服务地址
     */
    void suscribeComplete(boolean isSuccess, String serviceName, List<String> serviceAddress);
}
