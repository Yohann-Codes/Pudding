package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.common.protocol.Message;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTransportFactory;

import java.net.SocketAddress;

/**
 * The default implementation of {@link ClientService}.
 *
 * @author Yohann.
 */
public class DefaultClientService implements ClientService {
    private static final Logger logger = Logger.getLogger(DefaultClusterService.class);

    // Process the client(provider/consumer) task
    private static final Processor CLIENT_PROCESSOR = new ClientProcessor();

    private final Acceptor acceptor = NettyTransportFactory.createTcpAcceptor();

    private Channel channel;

    private volatile boolean isShutdown = false;

    public DefaultClientService() {
        acceptor.withProcessor(CLIENT_PROCESSOR);
    }

    @Override
    public Channel startRegistry(SocketAddress localAddress) {
        checkNotShutdown();

        try {
            synchronized (acceptor) {
                channel = acceptor.bind(localAddress);

                logger.info("start registry server: " + localAddress);
                return channel;
            }
        } catch (InterruptedException e) {
            logger.warn("start registry failed: " + localAddress, e);
        }

        return null; // Never get here
    }

    @Override
    public void closeRegistry() {
        channel.close();

        logger.info("close registry: " + channel);
        channel = null;
    }

    @Override
    public void shutdown() {
        closeRegistry();
        acceptor.shutdownGracefully();
        isShutdown = true;
    }

    private void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("the instance has shutdown");
        }
    }

    /**
     * The processor about client(provider/consumer).
     */
    private static class ClientProcessor implements Processor {

        @Override
        public void handleMessage(Channel channel, Message holder) {

        }
    }
}
