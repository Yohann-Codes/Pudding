package org.pudding.rpc;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.Message;
import org.pudding.common.utils.RandomUtil;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTransportFactory;

import java.net.SocketAddress;

/**
 * The default implementation of {@link RegistryService}.
 *
 * @author Yohann.
 */
public class DefaultRegistryService extends AbstractRegistryService {
    private static final Logger logger = Logger.getLogger(DefaultRegistryService.class);

    // Process the registry task
    private static final Processor REGISTRY_PROCESSOR = new RegistryProcessor();

    private final Connector connector = NettyTransportFactory.createTcpConnector();

    // Registry channel
    private Channel channel;
    private volatile boolean isConnected = false;

    public DefaultRegistryService() {
        connector.withProcessor(REGISTRY_PROCESSOR);
    }

    @Override
    public void connectRegistry(SocketAddress... address) {
        checkNotShutdown();

        int size = address.length;
        SocketAddress selectedAddress;

        if (size < 2) {
            selectedAddress = address[0];
        } else {
            // Select an address randomly
            int index = RandomUtil.getInt(size);
            selectedAddress = address[index];
        }

        synchronized (this) {
            if (isConnected) {
                throw new IllegalStateException("has connected with registry: " + channel);
            }
            try {
                // Connect to registry
                channel = connector.connect(selectedAddress);
                isConnected = true;

                logger.info("connect with registry: " + selectedAddress);
            } catch (InterruptedException e) {
                logger.warn("connect with registry failed: " + selectedAddress);
            }
        }
    }

    @Override
    public void disconnectRegistry() {
        synchronized (this) {
            if (!isConnected) {
                throw new IllegalStateException("not connect with registry");
            }
            channel.close();

            logger.info("disconnect with registry: " + channel);

            channel = null;
            isConnected = false;
        }
    }

    @Override
    protected void doRegister(ServiceMeta serviceMeta) {

    }

    @Override
    protected void doUnregister(ServiceMeta serviceMeta) {

    }

    @Override
    protected void doSubscribe(ServiceMeta serviceMeta) {

    }

    @Override
    public void shutdown() {
        super.shutdown();
        connector.shutdownGracefully();
    }

    /**
     * The processor about registry.
     */
    private static class RegistryProcessor implements Processor {

        @Override
        public void handleMessage(Channel channel, Message holder) {

        }
    }
}
