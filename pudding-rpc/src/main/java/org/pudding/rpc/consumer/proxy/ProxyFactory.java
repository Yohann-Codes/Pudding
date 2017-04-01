package org.pudding.rpc.consumer.proxy;

import org.pudding.rpc.consumer.config.ConsumerConfig;

import java.lang.reflect.Proxy;

/**
 * 代理服务工厂.
 *
 * @author Yohann.
 */
public class ProxyFactory {

    /**
     * 创建同步代理服务.
     * 用此代理调用方法为同步操作，会使请求线程阻塞.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T createSyncProxy(Class<T> clazz) {
        return createSyncProxy(clazz, ConsumerConfig.invokeTimeout());
    }

    /**
     * 创建同步代理服务.
     * 用此代理调用方法为同步操作，会使请求线程阻塞.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T createSyncProxy(Class<T> clazz, int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout: " + timeout);
        }

        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz}, new SyncInvocationHandler(timeout));
        return proxy;
    }

    /**
     * 创建异步代理服务.
     * 用此代理调用方法为异步操作，调用后直接返回，不会使线程阻塞.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T createAsyncProxy(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz}, new AsyncInvocationHandler());
        return proxy;
    }
}
