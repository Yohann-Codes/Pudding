package org.pudding.rpc.provider;

import org.pudding.common.exception.IllegalServiceException;
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
        return new ServiceMeta(interfaces[0].getSimpleName(), serviceAddress);
    }

    private void validate(Object service) {
        if (service == null) {
            throw new NullPointerException("service == null");
        }
    }
}
