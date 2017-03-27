package org.pudding.common.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 服务元数据.
 *
 * @author Yohann.
 */
public class ServiceMeta implements Serializable {

    // 服务名称（接口名称）
    private String name;
    // 方法
    private List<MethodMeta> methodMetas;
    // 服务地址 [host:port]
    private String address;

    public ServiceMeta(String name, List<MethodMeta> methodMetas, String address) {
        this.name = name;
        this.methodMetas = methodMetas;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public List<MethodMeta> getMethodMetas() {
        return methodMetas;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "ServiceMeta{" +
                "name='" + name + '\'' +
                ", methodMetas=" + methodMetas +
                ", address='" + address + '\'' +
                '}';
    }
}
