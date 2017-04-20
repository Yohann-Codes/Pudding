package org.pudding.example.service;

import org.pudding.rpc.consumer.invoker.future.InvokerFutureListener;

/**
 * @author Yohann.
 */
public class ServiceBImpl implements ServiceB {

    @Override
    public int subtract(int a, int b, InvokerFutureListener<Integer> listener) {
        return a - b;
    }
}
