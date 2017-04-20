package org.pudding.example.service;

import org.pudding.rpc.consumer.invoker.future.InvokerFutureListener;

import java.io.Serializable;

/**
 * @author Yohann.
 */
public interface ServiceC extends Serializable {
    int multiply(int a, int b, InvokerFutureListener<Integer> listener);
}
