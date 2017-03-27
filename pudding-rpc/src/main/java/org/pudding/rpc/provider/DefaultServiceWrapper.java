package org.pudding.rpc.provider;

import org.pudding.common.exception.IllegalServiceException;
import org.pudding.common.model.MethodMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.AddressUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的ServiceWrapper实现.
 *
 * @author Yohann.
 */
public class DefaultServiceWrapper implements ServiceWrapper {

    @Override
    public ServiceMeta build(Object service, String serviceAddress) {
        validate(service);
        // 检查地址格式
        AddressUtil.checkFormat(serviceAddress);
        // 检查服务是否合格
        Class<?> serviceClazz = service.getClass();
        Class<?>[] interfaces = serviceClazz.getInterfaces();
        if (interfaces.length != 1) {
            throw new IllegalServiceException("Service must implement one interface: " + service.getClass().getName());
        }
        return gather(interfaces[0], serviceAddress);
    }

    private ServiceMeta gather(Class<?> interfaceClazz, String serviceAddress) {
        List<MethodMeta> methodMetas = new ArrayList<>(); // 保存服务中的public方法

        Method[] methods = interfaceClazz.getMethods();
        for (Method m : methods) {
            if (!Modifier.isPublic(m.getModifiers())) {
                // 过滤掉非public方法
                continue;
            }
            methodMetas.add(new MethodMeta(m.getName(), m.getParameterTypes(), m.getReturnType()));
        }

        if (methodMetas.size() < 1) {
            throw new IllegalServiceException("At least one public method in the service: " + interfaceClazz.getName());
        }
        return new ServiceMeta(interfaceClazz.getName(), methodMetas, serviceAddress);
    }

    private void validate(Object service) {
        if (service == null) {
            throw new NullPointerException("service == null");
        }
    }
}
