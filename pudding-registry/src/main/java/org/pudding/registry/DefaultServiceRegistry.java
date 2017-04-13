package org.pudding.registry.config;

import org.apache.log4j.Logger;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.netty.NettyTcpAcceptor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * The default implementation of {@link PuddingServiceRegistry}.
 *
 * @author Yohann.
 */
public class DefaultServiceRegistry implements PuddingServiceRegistry {
    private static final Logger logger = Logger.getLogger(DefaultServiceRegistry.class);

    private final Acceptor acceptor = new NettyTcpAcceptor();

    @Override
    public Channel startRegistry(int port) {
        return startRegistry(new InetSocketAddress(port));
    }

    @Override
    public Channel startRegistry(SocketAddress address) {
        return null;
    }

    @Override
    public void joinUpCluster(String... address) {

    }

    @Override
    public void closeRegistry() {

    }
}
