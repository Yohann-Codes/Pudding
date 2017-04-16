package org.pudding.common.model;

import java.io.Serializable;

/**
 * Serivce meta data.
 *
 * @author Yohann.
 */
public class ServiceMeta implements Serializable {

    // 服务名称（接口全限定名）
    private String name;
    // 服务地址 (host:port)
    private String address;
    // 服务实例
    private Object instance;
    // 服务权重 (0, 100)
    private int weight;

    /**
     * 订阅服务时使用此构造方法，只需要服务的名称即可.
     */
    public ServiceMeta(String name) {
        this.name = name;
    }

    /**
     * 发布服务时使用此构造方法，需要服务的全部信息.
     */
    public ServiceMeta(String name, String address, Object instance, int weight) {
        this.name = name;
        this.address = address;
        this.instance = instance;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Object getInstance() {
        return instance;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof ServiceMeta) {
            ServiceMeta meta = (ServiceMeta) o;
            boolean name = meta.getName().equals(this.name);
            boolean address = meta.getAddress().equals(this.address);
            if (name && address) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ServiceMeta{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", instance=" + instance +
                ", weight=" + weight +
                '}';
    }
}
