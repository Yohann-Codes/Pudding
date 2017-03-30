package org.pudding.common.model;

import java.io.Serializable;

/**
 * 服务元数据.
 *
 * @author Yohann.
 */
public class ServiceMeta implements Serializable {

    // 服务名称（接口名称）
    private String name;
    // 服务地址 [host:port]
    private String address;

    public ServiceMeta(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServiceMeta) {
            ServiceMeta serviceMeta = (ServiceMeta) obj;
            if ((name.equals(serviceMeta.getName()))
                    && (address.equals(serviceMeta.getAddress()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ServiceMeta{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
