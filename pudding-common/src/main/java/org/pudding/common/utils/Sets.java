package org.pudding.common.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Provide some static methods to construct {@link java.util.Set} instances.
 *
 * @author Yohann.
 */
public class Sets {
    public static <T> Set<T> newHashSet() {
        return new HashSet<>();
    }
}
