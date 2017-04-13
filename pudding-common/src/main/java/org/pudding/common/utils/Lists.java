package org.pudding.common.utils;

import java.util.ArrayList;
import java.util.Iterator;
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

    public static <T> List<T> newArrayList(Iterator<T> iterator) {
        List<T> list = newArrayList();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
}
