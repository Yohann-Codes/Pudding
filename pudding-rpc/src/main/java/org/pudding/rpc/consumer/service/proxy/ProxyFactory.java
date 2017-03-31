package org.pudding.rpc.consumer.service.proxy;

import java.lang.reflect.Proxy;

/**
 * 代理服务工厂.
 *
 * @author Yohann.
 */
public class ProxyFactory {

    /**
     * 创建代理服务.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz}, new InvocationHandler());
        return proxy;
    }
}
