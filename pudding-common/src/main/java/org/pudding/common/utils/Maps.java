package org.pudding.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provide some static methods to construct {@link java.util.Map} instance.
 *
 * @author Yohann.
 */
public class Maps {
    /**
     * Create HashMap.
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * Create ConcurrentHashMap.
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<>();
    }
}
