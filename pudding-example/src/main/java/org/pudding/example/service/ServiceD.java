package org.pudding.example.service;

import org.pudding.rpc.consumer.invoker.future.InvokerFutureListener;

import java.io.Serializable;

/**
 * @author Yohann.
 */
public interface ServiceD extends Serializable {
    int divide(int a, int b, InvokerFutureListener<Integer> listener);
}
