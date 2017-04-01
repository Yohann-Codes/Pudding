package org.pudding.common.model;

/**
 * 用户需要构建的服务.
 *
 * @author Yohann.
 */
public class Service {
    // 服务名称（接口名称）
    private String name;
    // 服务地址 [host:port]
    private String address;
    // 服务实例
    private Object instance;

    public Service(String name, String address, Object instance) {
        this.name = name;
        this.address = address;
        this.instance = instance;
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

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", instance=" + instance +
                '}';
    }
}
