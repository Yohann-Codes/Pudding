package org.pudding.rpc.consumer.invoker;

import org.pudding.common.constant.LoadBalanceStrategy;
import org.pudding.rpc.RpcConfig;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;

/**
 * The proxy service factory.
 *
 * @author Yohann.
 */
public class ServiceProxyFactory {

    protected static ExecutorService executor;

    /**
     * Create a synchronous invocation proxy service.
     * <p>
     * Notice:
     * The request thread will be blocked when invoking a service.
     *
     * @param clazz
     * @param <T>
     */
    public static <T> T createSyncProxy(Class<T> clazz) {
        return createSyncProxy(clazz, RpcConfig.getInvokeTimeout(), RpcConfig.getLoadBalanceStrategy());
    }

    /**
     * Create a synchronous invocation proxy service.
     * <p>
     * Notice:
     * The request thread will be blocked when invoking a service.
     *
     * @param clazz
     * @param <T>
     * @param timeout
     */
    public static <T> T createSyncProxy(Class<T> clazz, int timeout) {
        return createSyncProxy(clazz, timeout, RpcConfig.getLoadBalanceStrategy());
    }

    /**
     * Create a synchronous invocation proxy service.
     * <p>
     * Notice:
     * The request thread will be blocked when invoking a service.
     *
     * @param clazz
     * @param <T>
     * @param loadBalanceStrategy
     */
    public static <T> T createSyncProxy(Class<T> clazz, LoadBalanceStrategy loadBalanceStrategy) {
        return createSyncProxy(clazz, RpcConfig.getInvokeTimeout(), loadBalanceStrategy);
    }

    /**
     * Create a synchronous invocation proxy service.
     * <p>
     * Notice:
     * The request thread will be blocked when invoking a service.
     *
     * @param clazz
     * @param <T>
     * @param timeout
     * @param loadBalanceStrategy
     */
    public static <T> T createSyncProxy(Class<T> clazz, int timeout, LoadBalanceStrategy loadBalanceStrategy) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout: " + timeout);
        }

        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz}, new SyncInvocationHandler(timeout, loadBalanceStrategy, executor));
        return proxy;
    }

    /**
     * Create a asynchronous invocation proxy service.
     *
     * @param clazz
     * @param <T>
     */
    public static <T> T createAsyncProxy(Class<T> clazz) {
        return createAsyncProxy(clazz, RpcConfig.getInvokeTimeout(), RpcConfig.getLoadBalanceStrategy());
    }

    /**
     * Create a asynchronous invocation proxy service.
     *
     * @param clazz
     * @param <T>
     * @param timeout
     */
    public static <T> T createAsyncProxy(Class<T> clazz, int timeout) {
        return createAsyncProxy(clazz, timeout, RpcConfig.getLoadBalanceStrategy());
    }

    /**
     * Create a asynchronous invocation proxy service.
     *
     * @param clazz
     * @param <T>
     * @param loadBalanceStrategy
     */
    public static <T> T createAsyncProxy(Class<T> clazz, LoadBalanceStrategy loadBalanceStrategy) {
        return createAsyncProxy(clazz, RpcConfig.getInvokeTimeout(), loadBalanceStrategy);
    }

    /**
     * Create a asynchronous invocation proxy service.
     *
     * @param clazz
     * @param <T>
     */
    public static <T> T createAsyncProxy(Class<T> clazz, int timeout, LoadBalanceStrategy loadBalanceStrategy) {
        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz}, new AsyncInvocationHandler(timeout, loadBalanceStrategy, executor));
        return proxy;
    }
}
