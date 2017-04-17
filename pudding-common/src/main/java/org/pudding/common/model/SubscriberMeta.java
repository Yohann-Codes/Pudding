package org.pudding.common.model;

import java.io.Serializable;

/**
 * Subscribe meta data.
 *
 * @author Yohann.
 */
public class SubscriberMeta implements Serializable {
    // 订阅的服务名
    private String serviceName;
    // /host
    private String subscriberHost;

    public SubscriberMeta(String serviceName, String subscriberHost) {
        this.serviceName = serviceName;
        this.subscriberHost = subscriberHost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getSubscriberHost() {
        return subscriberHost;
    }

    @Override
    public String toString() {
        return "SubscriberMeta{" +
                "serviceName='" + serviceName + '\'' +
                ", subscriberHost='" + subscriberHost + '\'' +
                '}';
    }
}
