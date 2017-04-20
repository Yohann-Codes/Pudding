package org.pudding.rpc;

import org.pudding.common.exception.IllegalServiceException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.AddressUtil;

import java.io.Serializable;

/**
 * The default implementation of {@link ServiceMetaFactory}.
 *
 * @author Yohann.
 */
public class DefaultMetaFactory implements ServiceMetaFactory {

    private static final ServiceMetaFactory FACTORY_INSTANCE = new DefaultMetaFactory();

    /**
     * Create a {@link ServiceMeta} for publishing service.
     *
     * @param service
     * @param serviceAddress
     * @param weight
     * @return ServiceMeta.
     */
    public static ServiceMeta createPublishMeta(Object service, String serviceAddress, int weight) {
        return FACTORY_INSTANCE.newPublishMeta(service, serviceAddress, weight);
    }

    /**
     * Create a {@link ServiceMeta} for subscribing service.
     *
     * @param serviceName
     * @return ServiceMeta
     */
    public static ServiceMeta createSubscribeMeta(String serviceName) {
        return FACTORY_INSTANCE.newSubscribeMeta(serviceName);
    }

    private DefaultMetaFactory() {
    }

    @Override
    public ServiceMeta newPublishMeta(Object service, String serviceAddress, int weight) {
        checkNotNull(service, "service instance");
        checkService(service);
        checkWeight(weight);
        AddressUtil.checkFormat(serviceAddress);

        return new ServiceMeta(
                service.getClass().getInterfaces()[0].getName(),
                serviceAddress,
                service,
                weight
        );
    }

    @Override
    public ServiceMeta newSubscribeMeta(String serviceName) {
        checkNotNull(serviceName, "service name");
        return new ServiceMeta(serviceName);
    }

    private void checkService(Object service) {
        Class<?> serviceClass = service.getClass();
        Class<?>[] interfaces = serviceClass.getInterfaces();
        if (interfaces.length != 1) {
            throw new IllegalServiceException("the service can only implement an interface" + service.getClass().getName());
        }
        if (interfaces[0].getInterfaces().length != 1) {
            throw new IllegalStateException("the service interface must extend java.io.Serializable");
        }
        if (!(interfaces[0].getInterfaces()[0].isAssignableFrom(Serializable.class))) {
            throw new IllegalStateException("the service interface must extend java.io.Serializable}");
        }
    }

    private void checkWeight(int weight) {
        if (weight < 1 || weight > 10) {
            throw new IllegalArgumentException("invalid weight: " + weight + ", must be (1, 10]");
        }
    }

    private <T> void checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
    }
}
