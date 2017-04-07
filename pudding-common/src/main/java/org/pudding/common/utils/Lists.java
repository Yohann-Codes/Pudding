package org.pudding.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide some static methods to construct {@link java.util.List} instances.
 *
 * @author Yohann.
 */
public class Lists {
    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }
}
