package org.pudding.common.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 方法元数据
 * 
 * @author Yohann.
 */
public class MethodMeta implements Serializable {
    private String name;
    private Class<?>[] paramTypes;
    private Class<?> returnType;

    public MethodMeta(String name, Class<?>[] paramTypes, Class<?> returnType) {
        this.name = name;
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "MethodMeta{" +
                "name='" + name + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", returnType=" + returnType +
                '}';
    }
}
