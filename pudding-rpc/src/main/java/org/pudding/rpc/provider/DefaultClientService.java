package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.transport.protocol.Message;
import org.pudding.common.utils.AddressUtil;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTransportFactory;

import java.util.concurrent.ExecutorService;

/**
 * The default implementation of {@link ClientService}.
 *
 * @author Yohann.
 */
public class DefaultClientService implements ClientService {
    private static final Logger logger = Logger.getLogger(DefaultClientService.class);

    // Process the client(consumer) task
    private final Processor clientProcessor = new ClientProcessor();

    private final Acceptor acceptor = NettyTransportFactory.createTcpAcceptor();

    private volatile boolean isShutdown = false;

    private final ExecutorService executor;

    public DefaultClientService(ExecutorService executor) {
        acceptor.withProcessor(clientProcessor);
        this.executor = executor;
    }

    @Override
    public Channel startService(ServiceMeta serviceMeta) {
        checkNotShutdown();

        String address = serviceMeta.getAddress();
        AddressUtil.checkFormat(address);
        int port = AddressUtil.port(address);

        Channel channel;
        try {
            channel = acceptor.bind(port);
            logger.info("start service; channel:" + channel);
            logger.info("start service: " + serviceMeta);
            return channel;
        } catch (InterruptedException e) {
            logger.warn("start service failed: " + serviceMeta, e);
        }

        return null; // Never get here
    }

    @Override
    public void stopService(ServiceMeta serviceMeta, Channel channel) {
        channel.close();
        logger.info("stop service, channel:" + channel);
        logger.info("stop service: " + serviceMeta);
    }

    @Override
    public void shutdown() {
        acceptor.shutdownGracefully();
        isShutdown = true;
    }

    private void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("the instance has shutdown");
        }
    }

    /**
     * The processor about client(consumer).
     */
    private class ClientProcessor implements Processor {

        @Override
        public void handleMessage(Channel channel, Message holder) {

        }

        @Override
        public void handleConnection(Channel channel) {

        }

        @Override
        public void handleDisconnection(Channel channel) {

        }
    }
}
