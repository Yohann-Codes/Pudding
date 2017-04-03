package org.pudding.rpc.utils;

import org.pudding.rpc.consumer.future.InvokeFutureListener;

import java.util.HashMap;
import java.util.Map;

/**
 * InvokeMap.
 *   key --> invokeId
 * value --> InvokeFutureListener
 *
 * @author Yohann.
 */
public class InvokeMap {
    private Map<String, InvokeFutureListener<?>> invokes;

    public InvokeMap() {
        invokes = new HashMap<>();
    }

    public <T> void put(long invokeId, InvokeFutureListener<T> listener) {
        invokes.put(key(invokeId), listener);
    }

    @SuppressWarnings("unchecked")
    public <T> InvokeFutureListener<T> get(long invokeId) {
        return (InvokeFutureListener<T>) invokes.get(key(invokeId));
    }

    public boolean containsKey(long invokeId) {
        return invokes.containsKey(key(invokeId));
    }

    public void remove(long invokeId) {
        invokes.remove(key(invokeId));
    }

    public int size() {
        return invokes.size();
    }

    private String key(long invokeId) {
        @SuppressWarnings("unchecked")
        String key = String.valueOf(invokeId);
        return key;
    }

    @Override
    public String toString() {
        return "InvokeMap{" +
                "invokes=" + invokes +
                '}';
    }
}
