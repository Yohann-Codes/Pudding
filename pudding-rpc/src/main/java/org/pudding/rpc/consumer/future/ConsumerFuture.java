package org.pudding.rpc.consumer.future;

/**
 * @author Yohann.
 */
public class ConsumerFuture<T extends FutureListener> {
    private T listener;

    @SuppressWarnings("unchecked")
    public void addFutureListener(T listener) {
        this.listener = listener;
    }

    public T getListener() {
        return listener;
    }
}
