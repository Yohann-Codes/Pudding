package org.pudding.rpc.consumer.invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Synchronous invocation handler.
 *
 * @author Yohann.
 */
public class SyncInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
