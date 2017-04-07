package org.pudding.common.utils;

import java.util.HashMap;
import java.util.Map;

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
}
