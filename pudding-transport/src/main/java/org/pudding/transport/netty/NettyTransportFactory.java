package org.pudding.transport.netty;

import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.TransportFactory;

/**
 * Netty transport factory.
 *
 * @author Yohann.
 */
public class NettyTransportFactory implements TransportFactory {

    private static final TransportFactory FACTORY_INSTANCE = new NettyTransportFactory();

    /**
     * Create a Netty {@link Acceptor} instance based TCP.
     */
    public static Acceptor createTcpAcceptor() {
        return FACTORY_INSTANCE.newTcpAcceptor();
    }

    /**
     * Create a Netty {@link Connector} instance based TCP.
     */
    public static Connector createTcpConnector() {
        return FACTORY_INSTANCE.newTcpConnector();
    }

    private NettyTransportFactory() {
    }

    @Override
    public Acceptor newTcpAcceptor() {
        return new NettyTcpAcceptor();
    }

    @Override
    public Connector newTcpConnector() {
        return new NettyTcpConnector();
    }
}
