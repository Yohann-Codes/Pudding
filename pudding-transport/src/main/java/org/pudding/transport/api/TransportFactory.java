package org.pudding.transport.api;

/**
 * Transport factory.
 *
 * @author Yohann.
 */
public interface TransportFactory {

    /**
     * New a {@link Acceptor} instance based TCP.
     */
    Acceptor newTcpAcceptor();

    /**
     * New a {@link Connector} instance based TCP.
     */
    Connector newTcpConnector();
}
