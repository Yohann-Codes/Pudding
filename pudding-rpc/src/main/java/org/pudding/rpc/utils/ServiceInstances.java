package org.pudding.rpc.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yohann.
 */
public class ServiceInstances {
    private static Map<String, Object> serviceInstances;

    static {
        serviceInstances = HashMap<>();
    }
}
