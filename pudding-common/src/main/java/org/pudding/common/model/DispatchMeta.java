package org.pudding.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yohann.
 */
public class DispatchMeta implements Serializable {
    private String serviceName;
    private List<ServiceMeta> serviceMetas;

    public DispatchMeta(String serviceName, List<ServiceMeta> serviceMetas) {
        this.serviceName = serviceName;
        this.serviceMetas = serviceMetas;
    }

    public String getServiceName() {
        return serviceName;
    }

    public List<ServiceMeta> getServiceMetas() {
        return serviceMetas;
    }
}
