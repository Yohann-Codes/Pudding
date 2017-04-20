package org.pudding.example.service;

import org.pudding.rpc.consumer.invoker.future.InvokerFutureListener;

/**
 * @author Yohann.
 */
public class ServiceDImpl implements ServiceD {
    @Override
    public int divide(int a, int b, InvokerFutureListener<Integer> listener) {
        return a / b;
    }
}
