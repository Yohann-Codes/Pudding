package org.pudding.transport.test;

import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.NettyConnector;

/**
 * @author Yohann.
 */
public class Client {
    public static void main(String[] args) {
        NettyConnector connector = new NettyConnector(new ClientProcessor());
        Future future = connector.connect("127.0.0.1", 20000);
        Channel channel = future.channel();
        Service service = new Service();
        service.setName("Service");
        channel.write(service);
    }

    static class Service {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
