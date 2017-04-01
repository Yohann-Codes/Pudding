package org.pudding.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * 同名的多个地址的服务.
 *
 * @author Yohann.
 */
public class Services implements Serializable {
    // 服务名称
    private String name;
    // 服务地址，可能有多个
    private List<ServiceMeta> serviceMetas;

    public void setName(String name) {
        this.name = name;
    }

    public void setServiceMetas(List<ServiceMeta> serviceMetas) {
        this.serviceMetas = serviceMetas;
    }

    public String getName() {
        return name;
    }

    public List<ServiceMeta> getServiceMetas() {
        return serviceMetas;
    }

    @Override
    public String toString() {
        return "SubscribeResult{" +
                "name='" + name + '\'' +
                ", serviceMetas=" + serviceMetas +
                '}';
    }
}
