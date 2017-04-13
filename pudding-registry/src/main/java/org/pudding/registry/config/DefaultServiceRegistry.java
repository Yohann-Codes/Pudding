package org.pudding.registry.config;

import org.apache.log4j.Logger;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.netty.NettyTcpAcceptor;

import java.net.SocketAddress;

/**
 * The default implementation of {@link ServiceRegistry}.
 *
 * @author Yohann.
 */
public class DefaultServiceRegistry implements ServiceRegistry {
    private static final Logger logger = Logger.getLogger(DefaultServiceRegistry.class);

    private final Acceptor acceptor = new NettyTcpAcceptor();

    @Override
    public Channel startRegistry(int port) {
        return null;
    }

    @Override
    public Channel startRegistry(SocketAddress address) {
        return null;
    }

    @Override
    public void closeRegistry() {

    }
}
