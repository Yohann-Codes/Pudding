package org.pudding.common.utils;

import java.util.Map;

/**
 * Constant Pool.
 *
 * @author Yohann.
 */
public abstract class ConstantPool<T> {

    private final Map<String, T> constants = Maps.newHashMap();

    public T valueOf(String name) {
        T c;

        synchronized (constants) {
            if (exists(name)) {
                c = constants.get(name);
            } else {
                c = newConstant0(name);
            }
        }

        return c;
    }

    private T newConstant0(String name) {
        if (exists(name)) {
            throw new IllegalArgumentException(name);
        }
        synchronized (constants) {
            T c = newInstant(name);
            constants.put(name, c);
            return c;
        }
    }

    protected abstract T newInstant(String name);

    public boolean exists(String name) {
        checkNotNull(name);

        synchronized (constants) {
            return constants.containsKey(name);
        }
    }

    private void checkNotNull(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
    }
}
