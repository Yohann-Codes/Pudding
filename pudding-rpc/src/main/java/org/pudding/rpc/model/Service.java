package org.pudding.rpc.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 发布服务的数据结构.
 *
 * @author Yohann.
 */
public class ServiceWrapper implements Serializable {
    // 服务名称（接口名称）
    private String serviceName;

    // 服务中可用方法
    // key: 方法名称
    // value: 重载方法参数List
    private Map<String, List<Class<?>[]>> methods;

    public ServiceWrapper(String serviceName, Map<String, List<Class<?>[]>> methods) {
        this.serviceName = serviceName;
        this.methods = methods;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Map<String, List<Class<?>[]>> getMethods() {
        return methods;
    }
}
