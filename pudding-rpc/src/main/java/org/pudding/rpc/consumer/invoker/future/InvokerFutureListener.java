package org.pudding.rpc.consumer.invoker.future;

import java.io.Serializable;

/**
 * @author Yohann.
 */
public interface InvokerFutureListener<T> extends Serializable {

    /**
     * Invocation successful.
     *
     * @param result
     */
    void success(T result);

    /**
     * Invocation failed.
     *
     * @param e
     */
    void failure(Exception e);
}
