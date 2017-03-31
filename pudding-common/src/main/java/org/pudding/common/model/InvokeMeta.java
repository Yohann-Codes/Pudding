package org.pudding.common.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 服务调用元数据.
 *
 * @author Yohann.
 */
public class InvokeMeta implements Serializable {
    // 服务名称
    private String serviceName;
    // 方法名称
    private String methodName;
    // 方法参数
    private Class<?>[] params;

    public InvokeMeta(String serviceName, String methodName, Class<?>[] params) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.params = params;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "InvokeMeta{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params=" + Arrays.asList(params) +
                '}';
    }
}
