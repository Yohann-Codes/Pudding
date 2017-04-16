package org.pudding.common.utils;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

/**
 * Concurrent set based {@link java.util.concurrent.ConcurrentHashMap}.
 *
 * @author Yohann.
 */
public class ConcurrentSet<E> {
    private final ConcurrentMap<E, Object> concurrentMap = Maps.newConcurrentHashMap();

    public void add(E e) {
        concurrentMap.put(e, new Object());
    }

    public void remove(E e) {
        concurrentMap.remove(e);
    }

    public Iterator<E> iterator() {
        return concurrentMap.keySet().iterator();
    }

    public int size() {
        return concurrentMap.size();
    }

    public boolean contains(E e) {
        return concurrentMap.containsKey(e);
    }
}
