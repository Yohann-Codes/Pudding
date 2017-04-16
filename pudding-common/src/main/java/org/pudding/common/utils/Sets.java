package org.pudding.common.utils;

/**
 * Provide some static methods to construct {@link java.util.Set} instances.
 *
 * @author Yohann.
 */
public class Sets {
    public static <E> ConcurrentSet<E> newConcurrentSet() {
        return new ConcurrentSet<>();
    }
}
