package org.pudding.rpc.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yohann.
 */
public class ResultMap {
    private Map<String, Object> results;

    public ResultMap() {
        results = new HashMap<>();
    }

    public void put(long invokeId, Object result) {
        results.put(key(invokeId), result);
    }

    public Object get(long invokeId) {
        return results.get(key(invokeId));
    }

    public boolean containsKey(long invokeId) {
        return results.containsKey(key(invokeId));
    }

    public void remove(long invokeId) {
        results.remove(key(invokeId));
    }

    public int size() {
        return results.size();
    }

    private String key(long invokeId) {
        @SuppressWarnings("unchecked")
        String key = String.valueOf(invokeId);
        return key;
    }
}
