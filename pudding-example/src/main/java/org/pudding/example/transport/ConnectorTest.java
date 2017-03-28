package org.pudding.example.transport;

import org.pudding.rpc.consumer.processor.ConsumerProcessor;
import org.pudding.transport.api.Connector;
import org.pudding.transport.netty.NettyConnector;

/**
 * @author Yohann.
 */
public class ConnectorTest {
    public static void main(String[] args) {
        Connector connector = new NettyConnector(ConsumerProcessor.PROCESSOR);
        connector.connect("127.0.0.1", 20000);
    }
}
