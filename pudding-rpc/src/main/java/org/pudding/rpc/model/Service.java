package org.pudding.rpc.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 发布服务的数据结构.
 *
 * @author Yohann.
 */
public class Service implements Serializable {

    // 服务名称（接口名称）
    private String name;

    // 服务中可用方法
    // key: 方法名称
    // value: 重载方法参数List
    private Map<String, List<Class<?>[]>> methods;

    // 服务地址 [host:port]
    private String address;

    public Service(String name, Map<String, List<Class<?>[]>> methods, String address) {
        this.name = name;
        this.methods = methods;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public Map<String, List<Class<?>[]>> getMethods() {
        return methods;
    }

    public String getAddress() {
        return address;
    }
}
