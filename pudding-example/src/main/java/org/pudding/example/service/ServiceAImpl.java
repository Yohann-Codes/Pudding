package org.pudding.example.service;

import org.pudding.rpc.consumer.invoker.future.InvokerFutureListener;

/**
 * @author Yohann.
 */
public class ServiceAImpl implements ServiceA {

    @Override
    public int add(int a, int b, InvokerFutureListener<Integer> listener) {
        return a + b;
    }
}
