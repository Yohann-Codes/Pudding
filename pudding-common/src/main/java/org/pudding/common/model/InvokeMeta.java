package org.pudding.common.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Invoke meta data.
 *
 * @author Yohann.
 */
public class InvokeMeta implements Serializable {

    // 服务名称
    private String serviceName;
    // 服务实例
    private Object serviceInstance;
    // 方法名称
    private String methodName;
    // 方法参数类型
    private Class<?>[] paramTypes;
    // 方法参数
    private Object[] params;

    public InvokeMeta(String serviceName, Object serviceInstance, String methodName, Class<?>[] paramTypes, Object[] params) {
        this.serviceName = serviceName;
        this.serviceInstance = serviceInstance;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.params = params;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Object getServiceInstance() {
        return serviceInstance;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "InvokeMeta{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceInstance='" + serviceInstance + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.asList(paramTypes) +
                ", params=" + Arrays.asList(params) +
                '}';
    }
}
