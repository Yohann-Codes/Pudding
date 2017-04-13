package org.pudding.common.utils;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Load service based on {@link java.util.ServiceLoader}.
 *
 * @author Yohann.
 */
public class ServiceLoaderUtil {

    /**
     * Load the first service.
     *
     * @param serviceClass
     * @param <T>
     */
    public static <T> T loadFirst(Class<T> serviceClass) {
        return ServiceLoader.load(serviceClass).iterator().next();
    }

    /**
     * Load all service.
     *
     * @param serviceClass
     * @param <T>
     */
    public static <T> List<T> loadAll(Class<T> serviceClass) {
        Iterator<T> iterator = ServiceLoader.load(serviceClass).iterator();
        return Lists.newArrayList(iterator);
    }
}
