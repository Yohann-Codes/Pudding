package org.pudding.example.service;

import org.pudding.rpc.consumer.invoker.future.InvokerFutureListener;

/**
 * @author Yohann.
 */
public class ServiceCImpl implements ServiceC {
    @Override
    public int multiply(int a, int b, InvokerFutureListener<Integer> listener) {
        return a * b;
    }
}
