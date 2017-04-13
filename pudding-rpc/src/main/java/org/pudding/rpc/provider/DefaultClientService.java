package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.Message;
import org.pudding.common.utils.AddressUtil;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTransportFactory;

/**
 * The default implementation of {@link ClientService}.
 *
 * @author Yohann.
 */
public class DefaultClientService implements ClientService {
    private static final Logger logger = Logger.getLogger(DefaultClientService.class);

    // Process the client(consumer) task
    private static final Processor CLIENT_PROCESSOR = new ClientProcessor();

    private final Acceptor acceptor = NettyTransportFactory.createTcpAcceptor();

    private volatile boolean isShutdown = false;

    public DefaultClientService() {
        acceptor.withProcessor(CLIENT_PROCESSOR);
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
    private static class ClientProcessor implements Processor {

        @Override
        public void handleMessage(Channel channel, Message holder) {

        }
    }
}
